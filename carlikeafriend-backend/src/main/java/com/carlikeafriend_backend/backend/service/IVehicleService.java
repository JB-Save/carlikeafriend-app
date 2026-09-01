package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.VehicleDTO;
import com.carlikeafriend_backend.backend.dto.VehicleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IVehicleService {

    VehicleResponseDTO saveVehicle(VehicleDTO vehicleDto) throws DuplicateResourceException;

    List<VehicleResponseDTO> getAllVehicles();

    Optional<VehicleResponseDTO> getVehicleById(Long id);

    VehicleResponseDTO updateVehicle(Long id, VehicleDTO vehicleDto) throws DuplicateResourceException;

    void deleteVehicle(Long id);

    VehicleResponseDTO restoreVehicle(String originalPlate) throws DuplicateResourceException;
}
