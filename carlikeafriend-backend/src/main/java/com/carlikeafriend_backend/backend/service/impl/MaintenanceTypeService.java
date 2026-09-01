package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.MaintenanceTypeCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.MaintenanceTypeDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.entity.MaintenanceType;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IMaintenanceTypeRepository;
import com.carlikeafriend_backend.backend.service.IMaintenanceTypeService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MaintenanceTypeService implements IMaintenanceTypeService {

    private static final Logger logger = LoggerFactory.getLogger(MaintenanceTypeService.class);

    private final IMaintenanceTypeRepository maintenanceTypeRepository;

    @Autowired
    public MaintenanceTypeService(IMaintenanceTypeRepository maintenanceTypeRepository) {
        this.maintenanceTypeRepository = maintenanceTypeRepository;
    }

    @Override
    @Transactional
    public SimpleResponseDTO saveMaintenanceType(MaintenanceTypeDTO maintenanceTypeDTO) throws DuplicateResourceException {

        String maintenanceCode = StringUtils.normalizeToUpperCase(maintenanceTypeDTO.getCode());

        logger.info("Intentando guardar nuevo tipo de mantenimiento: {}", maintenanceCode);

        // Validación de duplicados por nombre
        if (maintenanceTypeRepository.existsByCodeAndDeletedFalse(maintenanceCode)) {
            logger.warn("Ya existe un tipo de mantenimiento activo con el código: {}", maintenanceCode);
            throw new DuplicateResourceException("Ya existe un tipo de mantenimiento activo con el código: " + maintenanceCode);
        }

        // Mapear DTO a Entidad
        MaintenanceType maintenanceType = new MaintenanceType();
        maintenanceType.setCode(maintenanceCode);
        maintenanceType.setDescription(maintenanceTypeDTO.getDescription());

        MaintenanceType savedMaintenanceType = maintenanceTypeRepository.save(maintenanceType);
        logger.info("Tipo de mantenimiento guardado exitosamente con ID: {}", savedMaintenanceType.getId());
        return mapToDto(savedMaintenanceType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimpleResponseDTO> getAllMaintenanceTypes() {
        logger.info("Buscando todos los tipos de mantenimientos");
        return maintenanceTypeRepository.findAllByDeletedFalse().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<MaintenanceTypeCompleteResponseDTO> getMaintenanceTypeById(Long id) {
        logger.info("Buscando tipo de mantenimiento con ID: {}", id);
        return maintenanceTypeRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToCompleteDto);
    }

    @Override
    @Transactional
    public SimpleResponseDTO updateMaintenanceType(Long id, MaintenanceTypeDTO maintenanceTypeDTO) throws DuplicateResourceException {

        logger.info("Intentando actualizar tipo de mantenimiento con ID: {}", id);

        String maintenanceCode = StringUtils.normalizeToUpperCase(maintenanceTypeDTO.getCode());

        MaintenanceType existingMaintenanceType = maintenanceTypeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Tipo de mantenimiento no encontrado con ID: " + id));

        // Validar que el código del tipo de mantenimiento activo sea único, excluyendo el tipo actual
        if (maintenanceCode != null && !maintenanceCode.equals(StringUtils.normalizeToUpperCase(existingMaintenanceType.getCode()))) {
            if (maintenanceTypeRepository.existsByCodeAndIdNotAndDeletedFalse(maintenanceCode, id)) {
                throw new DuplicateResourceException("El código " + maintenanceCode + " ya está en uso por otro tipo de mantenimiento activo.");
            }
            //Actualizar datos básicos del tipo de política
            existingMaintenanceType.setCode(maintenanceCode);
        }

        Optional.ofNullable(maintenanceTypeDTO.getDescription()).ifPresent(existingMaintenanceType::setDescription);

        MaintenanceType updatedMaintenanceType = maintenanceTypeRepository.save(existingMaintenanceType);
        return mapToDto(updatedMaintenanceType);
    }

    @Override
    @Transactional
    public void deleteMaintenanceType(Long id) {
        MaintenanceType maintenanceType = maintenanceTypeRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de mantenimiento no encontrado con ID: " + id));


        String timestamp = String.valueOf(System.currentTimeMillis());
        maintenanceType.setCode(maintenanceType.getCode() + "_DELETED_" + timestamp);

        maintenanceType.setDeleted(true);

        maintenanceTypeRepository.save(maintenanceType);
        logger.warn("Tipo de mantenimiento con ID {} borrado lógicamente.", id);

    }

    private SimpleResponseDTO mapToDto(MaintenanceType maintenanceType) {
        return new SimpleResponseDTO(
                maintenanceType.getId(),
                maintenanceType.getCode()
        );
    }

    private MaintenanceTypeCompleteResponseDTO mapToCompleteDto(MaintenanceType maintenanceType) {
        return new MaintenanceTypeCompleteResponseDTO(
                maintenanceType.getId(),
                maintenanceType.getCode(),
                maintenanceType.getDescription()
        );
    }
}
