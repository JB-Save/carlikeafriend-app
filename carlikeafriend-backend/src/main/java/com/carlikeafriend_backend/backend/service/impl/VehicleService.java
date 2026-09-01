package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.*;
import com.carlikeafriend_backend.backend.exception.*;
import com.carlikeafriend_backend.backend.repository.*;
import com.carlikeafriend_backend.backend.service.IVehicleService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Service
public class VehicleService implements IVehicleService {

    private static final Logger logger = LoggerFactory.getLogger(VehicleService.class);

    private final IProductRepository productRepository;
    private final IVehicleRepository vehicleRepository;
    private final IBranchRepository branchRepository;

    @Autowired
    public VehicleService(IProductRepository productRepository,
                          IVehicleRepository vehicleRepository,
                          IBranchRepository branchRepository
    ) {
        this.productRepository = productRepository;
        this.vehicleRepository = vehicleRepository;
        this.branchRepository = branchRepository;
    }


    @Override
    @Transactional
    public VehicleResponseDTO saveVehicle(VehicleDTO vehicleDTO) throws DuplicateResourceException {

        String licensePlate = StringUtils.normalizeToUpperCase(vehicleDTO.getLicensePlate());
        String vin = StringUtils.normalizeToUpperCase(vehicleDTO.getVin());

        logger.info("Intentando guardar nuevo vehículo: {}", licensePlate);

        // Validación de duplicados por placa
        if (vehicleRepository.existsByLicensePlateAndDeletedFalse(licensePlate)) {
            logger.warn("Ya existe un vehículo activo con placa: {}", licensePlate);
            throw new DuplicateResourceException("Ya existe un vehículo activo con placa: " + licensePlate);
        }

        // Validación de duplicados por VIN
        if (vehicleRepository.existsByVinAndDeletedFalse(vin)) {
            logger.warn("Ya existe un vehículo activo con VIN: {}", vin);
            throw new DuplicateResourceException("Ya existe un vehículo activo con VIN: " + vin);
        }

        VehicleStatus vehicleStatus = VehicleStatus.validate(vehicleDTO.getStatus());

        // Mapear DTO a Entidad
        Vehicle vehicle = new Vehicle();
        vehicle.setLicensePlate(licensePlate);
        vehicle.setVin(vin);
        vehicle.setCurrentMileage(vehicleDTO.getCurrentMileage());
        vehicle.setColor(StringUtils.capitalize(vehicleDTO.getColor()));
        vehicle.setYear(vehicleDTO.getYear());
        vehicle.setVehicleStatus(vehicleStatus);

        // USO DE MÉTODOS AUXILIARES PARA SINCRONIZACIÓN
        updateProductAssociation(vehicle, vehicleDTO.getProductId());
        updateBranchAssociation(vehicle, vehicleDTO.getCurrentBranchId());

        Vehicle savedVehicle = vehicleRepository.save(vehicle);
        logger.info("Vehículo guardado con ID: {}", savedVehicle.getId());
        return mapToVehicleDto(savedVehicle);
    }

    @Override
    @Transactional(readOnly = true)
    public List<VehicleResponseDTO> getAllVehicles() {
        logger.info("Buscando todos los vehículos.");
        return vehicleRepository.findAllByDeletedFalse().stream()
                .map(this::mapToVehicleDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<VehicleResponseDTO> getVehicleById(Long id) {
        logger.info("Buscando vehículo con ID: {}", id);
        return vehicleRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToVehicleDto);
    }

    @Override
    @Transactional
    public VehicleResponseDTO updateVehicle(Long id, VehicleDTO vehicleDTO) throws DuplicateResourceException {
        logger.info("Intentando actualizar vehículo con ID: {}", id);

        String licensePlate = StringUtils.normalizeToUpperCase(vehicleDTO.getLicensePlate());
        String vin = StringUtils.normalizeToUpperCase(vehicleDTO.getVin());

        Vehicle existingVehicle = vehicleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Vehículo no encontrado con ID: " + id));

        // Validar que la placa del vehículo sea único entre los activos, excluyendo el vehículo actual
        if (licensePlate != null && !licensePlate.equals(StringUtils.normalizeToUpperCase(existingVehicle.getLicensePlate()))) {
            if (vehicleRepository.existsByLicensePlateAndIdNotAndDeletedFalse(licensePlate, id)) {
                throw new DuplicateResourceException("La placa " + licensePlate + " ya está en uso por otro vehículo activo.");
            }
            existingVehicle.setLicensePlate(licensePlate);
        }

        // Validar que el VIN del vehículo sea único entre los activos, excluyendo el VIN del vehículo actual
        if (vin != null && !vin.equals(StringUtils.normalizeToUpperCase(existingVehicle.getVin()))) {
            if (vehicleRepository.existsByVinAndIdNotAndDeletedFalse(vin, id)) {
                throw new DuplicateResourceException("El VIN " + vin + " ya está en uso por otro vehículo activo.");
            }
            existingVehicle.setVin(vin);
        }

        // Actualizar datos básicos del vehículo
        Optional.ofNullable(vehicleDTO.getCurrentMileage()).ifPresent(existingVehicle::setCurrentMileage);
        Optional.ofNullable(vehicleDTO.getColor()).map(StringUtils::capitalize).ifPresent(existingVehicle::setColor);
        Optional.ofNullable(vehicleDTO.getYear()).ifPresent(existingVehicle::setYear);
        Optional.ofNullable(vehicleDTO.getStatus()).ifPresent(st -> existingVehicle.setVehicleStatus(VehicleStatus.validate(st)));

        // USO DE MÉTODOS AUXILIARES PARA SINCRONIZACIÓN (Limpia y reasigna)
        updateProductAssociation(existingVehicle, vehicleDTO.getProductId());
        updateBranchAssociation(existingVehicle, vehicleDTO.getCurrentBranchId());

        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);
        return mapToVehicleDto(updatedVehicle);

    }

    @Override
    @Transactional
    public void deleteVehicle(Long id) {
        Vehicle vehicle = vehicleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con ID: " + id));

        if (!vehicle.isOutOfService()) {
            throw new DataIntegrityViolationException("No se puede eliminar: El vehículo no está 'Fuera de Servicio'.");
        }

        if (vehicle.hasPendingReservations()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen reservas activas asociados a este vehículo.");
        }

        String timestamp = String.valueOf(System.currentTimeMillis());

        // 1. Renombrar para liberar las restricciones UNIQUE de la base de datos
        vehicle.setLicensePlate(vehicle.getLicensePlate() + "_DELETE_" + timestamp);
        vehicle.setVin(vehicle.getVin() + "_DELETE_" + timestamp);


        // 2. Marcar como borrado
        vehicle.setDeleted(true);

        vehicleRepository.save(vehicle);
        logger.warn("Vehículo con ID {} borrado lógicamente.", id);
    }

    @Override
    @Transactional
    public VehicleResponseDTO restoreVehicle(String originalPlate) {
        logger.info("Intentando restaurar vehículo con placa : {}", originalPlate);

        // 1. Buscar en el historial de borrados
        Vehicle vehicle = vehicleRepository.findDeletedByLicensePlatePrefix(originalPlate)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró ningún registro borrado con la placa: " + originalPlate));

        // 2. Verificar que no exista YA otro vehículo ACTIVO con esa misma placa
        if (vehicleRepository.existsByLicensePlateAndDeletedFalse(originalPlate)) {
            throw new DuplicateResourceException("No se puede restaurar: Ya existe un nuevo vehículo activo con la placa " + originalPlate);
        }

        // 3. Restaurar valores originales (quitando el sufijo _DELETE_...)
        vehicle.setLicensePlate(originalPlate);
        vehicle.setVin(vehicle.getVin().split("_DELETE_")[0]);

        vehicle.setDeleted(false);

        Vehicle restoredVehicle = vehicleRepository.save(vehicle);
        return mapToVehicleDto(restoredVehicle);
    }

// --- MÉTODOS AUXILIARES ---
// Gestionan la integridad bidireccional y limpieza

    private void updateProductAssociation(Vehicle vehicle, Long productId) {
        if (productId == null) return;

        // Desvincular del producto anterior (si existe)
        if (vehicle.getProduct() != null) {
            vehicle.getProduct().removeVehicle(vehicle);
        }

        // Vincular nuevo
        Product newProduct = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con ID: " + productId));

        newProduct.addVehicle(vehicle); // Método de conveniencia de Product
    }

    private void updateBranchAssociation(Vehicle vehicle, Long branchId) {
        if (branchId == null) return;

        if (vehicle.getCurrentBranch() != null) {
            vehicle.getCurrentBranch().removeVehicle(vehicle);
        }

        Branch newBranch = branchRepository.findById(branchId)
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada con ID: " + branchId));

        newBranch.addVehicle(vehicle);
    }

    private VehicleResponseDTO mapToVehicleDto(Vehicle vehicle) {
        SimpleResponseDTO productDto = vehicle.getProduct() != null
                ? new SimpleResponseDTO(vehicle.getProduct().getId(), vehicle.getProduct().getName())
                : null;
        SimpleResponseDTO branchDto = vehicle.getCurrentBranch() != null
                ? new SimpleResponseDTO(vehicle.getCurrentBranch().getId(), vehicle.getCurrentBranch().getName())
                : null;

        return new VehicleResponseDTO(
                vehicle.getId(),
                vehicle.getLicensePlate(),
                vehicle.getVin(),
                vehicle.getCurrentMileage(),
                vehicle.getColor(),
                vehicle.getYear(),
                productDto,
                branchDto,
                vehicle.getVehicleStatus().toString()
        );
    }

}
