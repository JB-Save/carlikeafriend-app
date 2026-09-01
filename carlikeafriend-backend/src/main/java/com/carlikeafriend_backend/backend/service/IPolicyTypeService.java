package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.PolicyTypeDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IPolicyTypeService {

    SimpleResponseDTO savePolicyType(PolicyTypeDTO policyTypeDTO) throws DuplicateResourceException;

    List<SimpleResponseDTO> getAllPolicyTypes();

    Optional<SimpleResponseDTO> getPolicyTypeById(Long typeId);

    SimpleResponseDTO updatePolicyType(Long typeId, PolicyTypeDTO policyTypeDTO) throws DuplicateResourceException;

    void deletePolicyType(Long typeId);
}
