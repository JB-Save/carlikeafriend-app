package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.PolicyTypeDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.entity.PolicyType;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IPolicyTypeRepository;
import com.carlikeafriend_backend.backend.service.IPolicyTypeService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PolicyTypeService implements IPolicyTypeService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyTypeService.class);

    private final IPolicyTypeRepository policyTypeRepository;

    @Autowired
    public PolicyTypeService(IPolicyTypeRepository policyTypeRepository) {
        this.policyTypeRepository = policyTypeRepository;
    }

    @Override
    @Transactional
    public SimpleResponseDTO savePolicyType(PolicyTypeDTO policyTypeDTO) throws DuplicateResourceException {

        String policyTypeName = StringUtils.normalizeToUpperCase(policyTypeDTO.getName());

        logger.info("Intentando guardar nuevo tipo de política: {}", policyTypeName);

        // Validación de duplicados por nombre
        if (policyTypeRepository.existsByNameAndDeletedFalse(policyTypeName)) {
            logger.warn("Ya existe un tipo de política activa con el nombre: {}", policyTypeName);
            throw new DuplicateResourceException("Ya existe un tipo de política activa con el nombre: " + policyTypeName);
        }

        // Mapear DTO a Entidad
        PolicyType policyType = new PolicyType();
        policyType.setName(policyTypeName);

        PolicyType savedPolicyType = policyTypeRepository.save(policyType);
        logger.info("Tipo de política guardada exitosamente con ID: {}",savedPolicyType.getId());
        return mapToPolicyTypeDto(savedPolicyType);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SimpleResponseDTO> getAllPolicyTypes() {
        logger.info("Buscando todos los tipos de políticas");
        return policyTypeRepository.findAllByDeletedFalse().stream()
                .map(this::mapToPolicyTypeDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<SimpleResponseDTO> getPolicyTypeById(Long typeId) {
        logger.info("Buscando tipo de política con ID: {}", typeId);
        return policyTypeRepository.findByIdAndDeletedFalse(typeId)
                .map(this::mapToPolicyTypeDto);
    }

    @Override
    @Transactional
    public SimpleResponseDTO updatePolicyType(Long typeId, PolicyTypeDTO policyTypeDTO) throws DuplicateResourceException {

        logger.info("Intentando actualizar tipo de política con ID: {}", typeId);

        String policyTypeName = StringUtils.normalizeToUpperCase(policyTypeDTO.getName());

        PolicyType existingPolicyType = policyTypeRepository.findByIdAndDeletedFalse(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Tipo de política no encontrada con ID: " + typeId));

        // Validar que el nombre del tipo de política activo sea único, excluyendo el tipo actual
        if (policyTypeName != null && !policyTypeName.equals(StringUtils.normalizeToUpperCase(existingPolicyType.getName()))) {
            if(policyTypeRepository.existsByNameAndIdNotAndDeletedFalse(policyTypeName, typeId)) {
                throw new DuplicateResourceException("El nombre " + policyTypeName + " ya está en uso por otro tipo de política activo.");
            }
            //Actualizar datos básicos del tipo de política
            existingPolicyType.setName(policyTypeName);
        }

        PolicyType updatedPolicyType = policyTypeRepository.save(existingPolicyType);
        return mapToPolicyTypeDto(updatedPolicyType);
    }

    @Override
    @Transactional
    public void deletePolicyType(Long typeId) {
        PolicyType policyType = policyTypeRepository.findByIdAndDeletedFalse(typeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de política no encontrada con ID: " + typeId));

        if (policyType.hasActivePolicies()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen políticas activas asociadas a este tipo de política.");
        }

        String timestamp = String.valueOf(System.currentTimeMillis());
        policyType.setName(policyType.getName() + "_DELETED_" + timestamp);

        policyType.setDeleted(true);

        policyTypeRepository.save(policyType);
        logger.warn("Tipo de política ID {} borrada lógicamente. Nombre modificado para liberar restricción única.", typeId);

    }

    private SimpleResponseDTO mapToPolicyTypeDto(PolicyType policyType) {
        return new SimpleResponseDTO(
                policyType.getId(),
                policyType.getName()
        );
    }
}
