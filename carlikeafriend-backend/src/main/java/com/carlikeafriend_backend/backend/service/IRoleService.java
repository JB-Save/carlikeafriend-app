package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.RoleDTO;
import com.carlikeafriend_backend.backend.dto.RoleResponseDTO;
import com.carlikeafriend_backend.backend.dto.RoleResponseCompleteDTO;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface IRoleService {

    RoleResponseDTO saveRole(RoleDTO roleDTO) throws UniqueNameException;

    List<RoleResponseCompleteDTO> findAllRoles();

    Optional<RoleResponseCompleteDTO> findRoleById(Long id);

    RoleResponseDTO updateRole(Long id, RoleDTO roleDTO) throws UniqueNameException;

    void deleteRole(Long id);
}
