package com.carlikeafriend_backend.backend.service;


import com.carlikeafriend_backend.backend.dto.BranchDTO;
import com.carlikeafriend_backend.backend.dto.BranchCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IBranchService {

    SimpleResponseDTO saveBranch(BranchDTO branchDTO) throws DuplicateResourceException;

    List<BranchCompleteResponseDTO> getAllBranches();

    Optional<BranchCompleteResponseDTO> getBranchById(Long id);

    SimpleResponseDTO updateBranch(Long id, BranchDTO branchDTO) throws DuplicateResourceException;

    void deleteBranch(Long id);
}
