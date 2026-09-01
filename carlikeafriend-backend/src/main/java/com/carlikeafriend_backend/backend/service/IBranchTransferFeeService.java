package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.BranchTransferFeeDTO;
import com.carlikeafriend_backend.backend.dto.BranchTransferFeeResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IBranchTransferFeeService {

    BranchTransferFeeResponseDTO saveBranchTransferFee(BranchTransferFeeDTO branchTransferFeeDTO) throws DuplicateResourceException;

    List<BranchTransferFeeResponseDTO> getAllBranchTransferFees();

    Optional<BranchTransferFeeResponseDTO> getBranchTransferFeeById(Long id);

    List<BranchTransferFeeResponseDTO> getBranchTransferFeeByOriginBranchId(Long originBranchId);

    BranchTransferFeeResponseDTO updateBranchTransferFee(Long id, BranchTransferFeeDTO branchTransferFeeDTO) throws DuplicateResourceException;

    void deleteBranchTransferFee(Long id);

}
