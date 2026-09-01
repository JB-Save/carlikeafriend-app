package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.InspectionDTO;
import com.carlikeafriend_backend.backend.dto.InspectionResponseDTO;
import com.carlikeafriend_backend.backend.entity.*;
import com.carlikeafriend_backend.backend.exception.InspectionStateConflictException;
import com.carlikeafriend_backend.backend.exception.InvalidRangeException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IInspectionRepository;
import com.carlikeafriend_backend.backend.repository.IReservationRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.service.IInspectionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InspectionService implements IInspectionService {

    private static final Logger logger = LoggerFactory.getLogger(InspectionService.class);

    private final IInspectionRepository inspectionRepository;
    private final IReservationRepository reservationRepository;
    private final IUserRepository userRepository;

    @Autowired
    public InspectionService(IInspectionRepository inspectionRepository,
                                 IReservationRepository reservationRepository,
                                 IUserRepository userRepository) {
        this.inspectionRepository = inspectionRepository;
        this.reservationRepository = reservationRepository;
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public InspectionResponseDTO createInspection(Long inspectorId, InspectionDTO request) {

        logger.info("Intentando crear nueva Inspección a la reserva : {}", request.getReservationId());

        // 1. Obtener entidades
        User inspector = userRepository.findByIdAndDeletedFalse(inspectorId)
                .orElseThrow(() -> new ResourceNotFoundException("Inspector no encontrado"));

        Reservation reservation = reservationRepository.findById(request.getReservationId())
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada"));

        //Validar tipo de Inspección
        InspectionType inspectionType = InspectionType.validate(request.getInspectionType());

        Vehicle vehicle = reservation.getVehicle();

        // 2. VALIDACIONES DE REGLAS DE NEGOCIO

        // A. Validar coherencia de daños
        if (Boolean.TRUE.equals(request.getHasDamage()) &&
                (request.getDamageDescription() == null || request.getDamageDescription().isBlank())) {
            throw new InspectionStateConflictException("Si se reportan daños, la descripción es obligatoria.");
        }

        // B. Validar coherencia de kilometraje según el tipo de inspección
        if (inspectionType == InspectionType.PICKUP) {
            if (request.getMileage() < vehicle.getCurrentMileage()) {
                throw new InvalidRangeException("El kilometraje de entrega no puede ser menor al kilometraje actual del vehículo (" + vehicle.getCurrentMileage() + ").");
            }
        } else if (inspectionType == InspectionType.RETURN) {
            // Buscamos la inspección de salida para comparar
            Inspection pickupInspection = reservation.getInspections().stream()
                    .filter(i -> i.getInspectionType() == InspectionType.PICKUP)
                    .findFirst()
                    .orElseThrow(() -> new InspectionStateConflictException("No se puede hacer inspección de retorno sin una inspección de entrega previa."));

            if (request.getMileage() < pickupInspection.getMileage()) {
                throw new InvalidRangeException("El kilometraje de retorno no puede ser menor al de entrega (" + pickupInspection.getMileage() + ").");
            }
        }

        // 3. Crear la entidad Inspection
        Inspection inspection = new Inspection();
        inspection.setInspector(inspector);
        inspection.setInspectionType(inspectionType);
        inspection.setMileage(request.getMileage());
        inspection.setHasDamage(request.getHasDamage());
        inspection.setDamageDescription(request.getHasDamage() ? request.getDamageDescription() : null);
        inspection.setFuelLevel(request.getFuelLevel());

        // 4. VALIDACIONES DE DOMINIO (Usando los métodos en Reservation)
        if (reservation.inspectionTypeAlreadyExists(inspection)) {
            throw new InspectionStateConflictException("La reserva ya cuenta con una inspección de tipo: " + inspection.getInspectionType());
        }
        if (reservation.isInspectionsComplete()) {
            throw new InspectionStateConflictException("La reserva ya tiene el máximo de inspecciones permitidas (Entrega y Devolución).");
        }

        // Vincular bidireccionalmente
        reservation.addInspection(inspection);

        // 5. CERRAR EL CICLO DE LOS SNAPSHOTS PENDIENTES
        if (inspection.getInspectionType() == InspectionType.PICKUP) {
            reservation.setFuelLevelAtPickupSnapshot(inspection.getFuelLevel());
        } else if (inspection.getInspectionType() == InspectionType.RETURN) {
            reservation.setFuelLevelAtReturnSnapshot(inspection.getFuelLevel());
        }

        // Guardar. Al guardar la reserva con cascade, se guarda la inspección.
        // O podemos guardar la inspección explícitamente.
        Inspection savedInspection = inspectionRepository.save(inspection);
        reservationRepository.save(reservation);

        return mapToInspectionDTO(savedInspection);
    }

    @Override
    @Transactional(readOnly = true)
    public List<InspectionResponseDTO> getInspectionsByReservation(UUID reservationId) {
        return inspectionRepository.findByReservationId(reservationId).stream()
                .map(this::mapToInspectionDTO)
                .collect(Collectors.toList());
    }

    private InspectionResponseDTO mapToInspectionDTO(Inspection ins) {
        return new InspectionResponseDTO(
                ins.getId(),
                ins.getInspectionType().name(),
                ins.getMileage(),
                ins.getHasDamage(),
                ins.getDamageDescription(),
                ins.getFuelLevel(),
                ins.getInspector().getName() + " " + ins.getInspector().getLastName(),
                ins.getCreatedAt()
        );
    }

}
