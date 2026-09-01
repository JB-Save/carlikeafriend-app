package com.carlikeafriend_backend.backend.service;


import com.carlikeafriend_backend.backend.dto.PermissionDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.dto.PermissionCompleteResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IPermissionService {

    SimpleResponseDTO savePermission(PermissionDTO permissionDTO) throws DuplicateResourceException;

    List<PermissionCompleteResponseDTO> getAllPermissions();

    Optional<PermissionCompleteResponseDTO> getPermissionById(Long id);

    SimpleResponseDTO updatePermission(Long id, PermissionDTO permissionDTO) throws DuplicateResourceException;

    void deletePermission(Long id);
}
