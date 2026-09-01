package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.PolicyCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.PolicyDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.entity.PolicyType;
import com.carlikeafriend_backend.backend.entity.Policy;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IPolicyTypeRepository;
import com.carlikeafriend_backend.backend.repository.IPolicyRepository;
import com.carlikeafriend_backend.backend.service.IPolicyService;
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
public class PolicyService implements IPolicyService {

    private static final Logger logger = LoggerFactory.getLogger(PolicyService.class);

    private final IPolicyRepository policyRepository;
    private final IPolicyTypeRepository policyTypeRepository;

    @Autowired
    public PolicyService(IPolicyRepository policyRepository, IPolicyTypeRepository policyTypeRepository) {
        this.policyRepository = policyRepository;
        this.policyTypeRepository = policyTypeRepository;
    }


    @Override
    @Transactional
    public SimpleResponseDTO savePolicy(PolicyDTO policyDTO) throws DuplicateResourceException {

        String policyName = StringUtils.capitalize(policyDTO.getName());

        logger.info("Intentando guardar nueva política: {}", policyName);

        // Validación de duplicados por nombre
        if (policyRepository.existsByNameAndDeletedFalse(policyName)) {
            logger.warn("Ya existe una política activa con el nombre: {}", policyName);
            throw new DuplicateResourceException("Ya existe una política activa con el nombre: " + policyName);
        }

        // Mapear DTO a Entidad
        Policy policy = new Policy();
        policy.setName(policyName);
        policy.setContent(policyDTO.getContent());

        // Usar método auxiliar
        updatePolicyTypeAssociation(policy, policyDTO.getPolicyTypeId());

        Policy savedPolicy = policyRepository.save(policy);
        logger.info("Política guardada exitosamente con ID: {}", savedPolicy.getId());
        return mapToPolicyDto(savedPolicy);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PolicyCompleteResponseDTO> getAllPolicies() {
        logger.info("Buscando todas las políticas.");
        return policyRepository.findAllByDeletedFalse().stream()
                .map(this::mapToPolicyCompleteDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PolicyCompleteResponseDTO> getPolicyById(Long id) {
        logger.info("Buscando política con ID: {}", id);
        return policyRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToPolicyCompleteDto);
    }

    @Override
    @Transactional
    public SimpleResponseDTO updatePolicy(Long id, PolicyDTO policyDTO) throws DuplicateResourceException {
        logger.info("Intentando actualizar política con ID: {}", id);

        String policyName = StringUtils.capitalize(policyDTO.getName());

        Policy existingPolicy = policyRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Política no encontrada con ID: " + id));

        // Validar que el nombre de la política sea único, excluyendo la actual
        if (policyName != null && !policyName.equals(StringUtils.capitalize(existingPolicy.getName()))) {
            if(policyRepository.existsByNameAndIdNotAndDeletedFalse(policyName, id)) {
                throw new DuplicateResourceException("El nombre " + policyName + " ya está en uso por otra política activa.");
            }
            existingPolicy.setName(policyName);
        }

        // Actualizar datos básicos
        Optional.ofNullable(policyDTO.getContent()).ifPresent(existingPolicy::setContent);

        // USO DE MÉTODOS AUXILIARES PARA SINCRONIZACIÓN (Limpia y reasigna)
        updatePolicyTypeAssociation(existingPolicy, policyDTO.getPolicyTypeId());

        Policy updatedPolicy = policyRepository.save(existingPolicy);
        return mapToPolicyDto(updatedPolicy);
    }

    @Override
    @Transactional
    public void deletePolicy(Long id) {
        Policy policy = policyRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Política no encontrada con ID: " + id));

        if (policy.hasActiveProducts()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen productos activos asociados a esta política.");
        }

        // Liberar el nombre único para futuras creaciones
        String timestamp = String.valueOf(System.currentTimeMillis());
        policy.setName(policy.getName() + "_DELETED_" + timestamp);

        // Borrado lógico
        policy.setDeleted(true);

        policyRepository.save(policy);
        logger.info("Política con ID: {} borrada lógicamente.", id);
    }

    // MÉTODOS AUXILIARES
    private void updatePolicyTypeAssociation(Policy policy, Long policyTypeId) {
        if (policyTypeId == null) return;

        // Desvincular del tipo de política anterior (si existe)
        if (policy.getPolicyType() != null) {
            policy.getPolicyType().removePolicy(policy);
        }

        // Vincular nueva
        PolicyType newPolicyType = policyTypeRepository.findById(policyTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Tipo de política no encontrada con ID: " + policyTypeId));

        newPolicyType.addPolicy(policy); // Método de conveniencia de PolicyType
    }


    private SimpleResponseDTO mapToPolicyDto(Policy policy) {
        return new SimpleResponseDTO(policy.getId(), policy.getName());
    }

    private PolicyCompleteResponseDTO mapToPolicyCompleteDto(Policy policy) {
        SimpleResponseDTO policyTypeDto = policy.getPolicyType() != null
                ? new SimpleResponseDTO(policy.getPolicyType().getId(), policy.getPolicyType().getName())
                : null;
        return new PolicyCompleteResponseDTO(policy.getId(), policy.getName(), policy.getContent(), policyTypeDto);
    }

}
