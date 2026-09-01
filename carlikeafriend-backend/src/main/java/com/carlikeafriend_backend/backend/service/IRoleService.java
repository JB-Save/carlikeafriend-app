package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.RoleCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.RoleDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;

import java.util.List;
import java.util.Optional;

public interface IRoleService {

    SimpleResponseDTO saveRole(RoleDTO roleDTO) throws DuplicateResourceException;

    List<RoleCompleteResponseDTO> getAllRoles();

    Optional<RoleCompleteResponseDTO> getRoleById(Long id);

    SimpleResponseDTO updateRole(Long id, RoleDTO roleDTO) throws DuplicateResourceException;

    void deleteRole(Long id);
}
