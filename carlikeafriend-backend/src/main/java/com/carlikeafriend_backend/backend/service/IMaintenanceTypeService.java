package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.MaintenanceTypeCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.MaintenanceTypeDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IMaintenanceTypeService {

    SimpleResponseDTO saveMaintenanceType(MaintenanceTypeDTO maintenanceTypeDTO) throws DuplicateResourceException;

    List<SimpleResponseDTO> getAllMaintenanceTypes();

    Optional<MaintenanceTypeCompleteResponseDTO> getMaintenanceTypeById(Long id);

    SimpleResponseDTO updateMaintenanceType(Long id, MaintenanceTypeDTO maintenanceTypeDTO) throws DuplicateResourceException;

    void deleteMaintenanceType(Long id);

}
