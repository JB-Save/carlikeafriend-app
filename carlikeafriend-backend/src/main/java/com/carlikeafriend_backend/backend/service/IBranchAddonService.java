package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.AddonResponseDTO;
import com.carlikeafriend_backend.backend.dto.BranchAddonDTO;
import com.carlikeafriend_backend.backend.dto.BranchInventoryResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface IBranchAddonService {

    void assignStockToBranch(BranchAddonDTO request);

    // El método estrella para el frontend
    List<AddonResponseDTO> getAvailableAddonsForDates(Long branchId, LocalDateTime pickupDate, LocalDateTime returnDate);

    List<BranchInventoryResponseDTO> getInventoryByBranchId(Long branchId);

}
