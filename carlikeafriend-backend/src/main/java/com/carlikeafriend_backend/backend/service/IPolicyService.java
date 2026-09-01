package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.PolicyCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.PolicyDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IPolicyService {


    SimpleResponseDTO savePolicy(PolicyDTO policyDTO) throws DuplicateResourceException;

    List<PolicyCompleteResponseDTO> getAllPolicies();

    Optional<PolicyCompleteResponseDTO> getPolicyById(Long id);

    SimpleResponseDTO updatePolicy(Long id, PolicyDTO policyDTO) throws DuplicateResourceException;

    void deletePolicy(Long id);

}
