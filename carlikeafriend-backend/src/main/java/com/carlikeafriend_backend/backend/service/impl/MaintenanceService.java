package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.MaintenanceDTO;
import com.carlikeafriend_backend.backend.dto.MaintenanceResponseDTO;
import com.carlikeafriend_backend.backend.entity.MaintenanceLog;
import com.carlikeafriend_backend.backend.entity.MaintenanceType;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.entity.Vehicle;
import com.carlikeafriend_backend.backend.exception.InvalidRangeException;
import com.carlikeafriend_backend.backend.exception.MaintenanceStateConflictException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IMaintenanceLogRepository;
import com.carlikeafriend_backend.backend.repository.IMaintenanceTypeRepository;
import com.carlikeafriend_backend.backend.repository.IUserRepository;
import com.carlikeafriend_backend.backend.repository.IVehicleRepository;
import com.carlikeafriend_backend.backend.service.IMaintenanceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MaintenanceService implements IMaintenanceService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceService.class);

    private final IMaintenanceLogRepository maintenanceRepository;
    private final IVehicleRepository vehicleRepository;
    private final IUserRepository userRepository;
    private final IMaintenanceTypeRepository maintenanceTypeRepository;

    @Autowired
    public MaintenanceService(IMaintenanceLogRepository maintenanceRepository,
                                  IVehicleRepository vehicleRepository,
                                  IUserRepository userRepository,
                              IMaintenanceTypeRepository maintenanceTypeRepository) {
        this.maintenanceRepository = maintenanceRepository;
        this.vehicleRepository = vehicleRepository;
        this.userRepository = userRepository;
        this.maintenanceTypeRepository = maintenanceTypeRepository;
    }

    @Override
    @Transactional
    public void sendVehicleToMaintenance(Long vehicleId) {

        logger.info("Intentando enviar el vehículo con ID: {} a mantenimiento.", vehicleId);

        Vehicle vehicle = vehicleRepository.findByIdAndDeletedFalse(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con ID: "+ vehicleId));

        // 1. Validar reglas de dominio y cambiar estado
        if(vehicle.isRented()){
            throw new MaintenanceStateConflictException("No se puede enviar a mantenimiento un vehículo que está actualmente rentado.");
        }
        vehicle.sendToMaintenance();

        // 2. Guardar el nuevo estado
        vehicleRepository.save(vehicle);
    }

    @Override
    @Transactional
    public MaintenanceResponseDTO registerMaintenanceCompleted(Long vehicleId, Long technicianId, MaintenanceDTO request) {

        logger.info("Intentando registrar mantenimiento completado al vehículo con ID: {}.", vehicleId);

        Vehicle vehicle = vehicleRepository.findByIdAndDeletedFalse(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con ID: " + vehicleId));

        User technician = userRepository.findByIdAndDeletedFalse(technicianId)
                .orElseThrow(() -> new ResourceNotFoundException("Técnico no encontrado."));

        MaintenanceType maintenanceType = maintenanceTypeRepository.findByIdAndDeletedFalse(request.getMaintenanceType())
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de mantenimiento no encontrado con ID: " + request.getMaintenanceType()));

        // 1. Validar lógica (El vehículo debe estar en mantenimiento)
        if(!vehicle.isInMaintenance()){
            throw new MaintenanceStateConflictException("El vehículo no se encuentra en estado de mantenimiento.");
        }

        // 2. Actualizar el kilometraje del vehículo si el mantenimiento reporta uno mayor
        if (request.getMileageAtMaintenance() > vehicle.getCurrentMileage()) {
            vehicle.setCurrentMileage(request.getMileageAtMaintenance());
        } else if (request.getMileageAtMaintenance() < vehicle.getCurrentMileage()) {
            throw new InvalidRangeException("El kilometraje del mantenimiento no puede ser menor al kilometraje actual del vehículo.");
        }

        //Liberar el vehículo
        vehicle.release();

        // 3. Crear el registro
        MaintenanceLog log = new MaintenanceLog();
        log.setVehicle(vehicle);
        log.setTechnician(technician);
        log.setMaintenanceType(maintenanceType);
        log.setDescription(request.getDescription());
        log.setCost(request.getCost());
        log.setMileageAtMaintenance(request.getMileageAtMaintenance());
        log.setMaintenanceDate(request.getMaintenanceDate());

        // 4. Guardar todo
        MaintenanceLog savedLog = maintenanceRepository.save(log);
        vehicleRepository.save(vehicle);

        return new MaintenanceResponseDTO(
                savedLog.getId(),
                vehicle.getLicensePlate(),
                savedLog.getMaintenanceType().getCode(),
                savedLog.getDescription(),
                savedLog.getCost(),
                savedLog.getMileageAtMaintenance(),
                savedLog.getMaintenanceDate(),
                technician.getName() + " " + technician.getLastName()
        );
    }


}
