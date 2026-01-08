package com.carlikeafriend_backend.backend.service;


import com.carlikeafriend_backend.backend.dto.PermissionDTO;
import com.carlikeafriend_backend.backend.dto.PermissionResponseDTO;
import com.carlikeafriend_backend.backend.dto.PermissionResponseCompleteDTO;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface IPermissionService {

    PermissionResponseDTO savePermission(PermissionDTO permissionDTO) throws UniqueNameException;

    List<PermissionResponseCompleteDTO> findAllPermissions();

    Optional<PermissionResponseCompleteDTO> findPermissionById(Long id);

    PermissionResponseDTO updatePermission(Long id, PermissionDTO permissionDTO) throws UniqueNameException;

    void deletePermission(Long id);
}
