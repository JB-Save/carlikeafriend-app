package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.*;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.*;
import com.carlikeafriend_backend.backend.service.IRoleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RoleService implements IRoleService {

    private static final Logger logger = LoggerFactory.getLogger(RoleService.class);

    private final IRoleRepository roleRepository;
    private final IPermissionRepository permissionRepository;

    @Autowired
    public RoleService(IRoleRepository roleRepository, IPermissionRepository permissionRepository) {
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
    }


    @Override
    @Transactional
    public RoleResponseDTO saveRole(RoleDTO roleDTO) throws UniqueNameException {

        logger.info("Intentando guardar nuevo rol: {}", roleDTO.getName());

        // Validación de duplicados por nombre
        if (roleRepository.existsByName(roleDTO.getName())) {
            logger.warn("El nombre del rol ya existe: {}", roleDTO.getName());
            throw new UniqueNameException("El nombre del role ya existe.");
        }

        // Mapear DTO a Entidad
        Role role = new Role();
        role.setName(roleDTO.getName());
        role.setDescription(roleDTO.getDescription());

        //Asociar Permisos
        Set<Permission> managedPermissions = resolvePermissions(roleDTO.getPermissions());
        if(!managedPermissions.isEmpty()){
            managedPermissions.forEach(perm -> perm.addRole(role));
        }

        Role savedRole = roleRepository.save(role);
        logger.info("Rol guardado con ID: {}", savedRole.getId());
        return convertToDto(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponseCompleteDTO> findAllRoles() {
        logger.info("Buscando todos los roles.");
        return roleRepository.findAll().stream()
                .map(this::convertRoleResponseCompleteDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleResponseCompleteDTO> findRoleById(Long id) {
        logger.info("Buscando rol con ID: {}", id);
        return roleRepository.findById(id)
                .map(this::convertRoleResponseCompleteDto);
    }

    @Override
    @Transactional
    public RoleResponseDTO updateRole(Long id, RoleDTO roleDTO) throws UniqueNameException {
        logger.info("Intentando actualizar rol con ID: {}", id);

        Role existingRole = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role no encontrado con ID: " + id));

        // Validar que el nombre del rol sea único, excluyendo el rol actual
        if (roleDTO.getName() != null && !roleDTO.getName().equals(existingRole.getName()) && roleRepository.existsByNameAndIdNot(roleDTO.getName(), id)) {
            throw new UniqueNameException("El nombre del rol ya existe.");
        }

        // Actualizar datos básicos del rol
        Optional.ofNullable(roleDTO.getName()).ifPresent(existingRole::setName);
        Optional.ofNullable(roleDTO.getDescription()).ifPresent(existingRole::setDescription);

        // Actualizar relaciones (Lógica simplificada usando métodos auxiliares)
        updatePermissions(existingRole, roleDTO.getPermissions());

        Role updatedRole = roleRepository.save(existingRole);
        return convertToDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));

        roleRepository.delete(role);
        logger.warn("Rol eliminado con ID: {}", id);
    }

    // Métodos Auxiliares para limpiar el código principal

    private Set<Permission> resolvePermissions(Set<Long> ids) {
        Set<Permission> permissions = new HashSet<>();
        if (ids != null) {
            ids.stream().filter(Objects::nonNull).forEach(id -> {
                permissions.add(permissionRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id)));
            });
        }
        return permissions;
    }

    private void updatePermissions(Role role, Set<Long> newIds) {
        if (newIds == null) return;
        Set<Long> finalIds = newIds.stream().filter(Objects::nonNull).collect(Collectors.toSet());

        // Eliminar las que no están
       role.getPermissions().removeIf(c -> !finalIds.contains(c.getId()));

        // Agregar las nuevas
        for (Long id : finalIds) {
            if (role.getPermissions().stream().noneMatch(c -> c.getId().equals(id))) {
               role.getPermissions().add(permissionRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id)));
            }
        }
    }


    private RoleResponseDTO convertToDto(Role role) {
        List<PermissionResponseDTO> permissionDtos = new ArrayList<>();
        PermissionResponseDTO permissionDto = null;
        if (role.getPermissions() != null) {
            for (Permission permission : role.getPermissions()) {
                if (permission != null) {
                    permissionDto = new PermissionResponseDTO();
                    permissionDto.setId(permission.getId());
                    permissionDto.setName(permission.getName());
                    permissionDtos.add(permissionDto);
                }
            }
        }

        return new RoleResponseDTO(
                role.getId(),
                role.getName()
        );
    }

    private RoleResponseCompleteDTO convertRoleResponseCompleteDto(Role role) {
        List<PermissionResponseDTO> permissionDtos = new ArrayList<>();
        PermissionResponseDTO permissionDto = null;
        if (role.getPermissions() != null) {
            for (Permission permission : role.getPermissions()) {
                if (permission != null) {
                    permissionDto = new PermissionResponseDTO();
                    permissionDto.setId(permission.getId());
                    permissionDto.setName(permission.getName());
                    permissionDtos.add(permissionDto);
                }
            }
        }

        return new RoleResponseCompleteDTO(
                role.getId(),
                role.getName(),
                role.getDescription(),
                permissionDtos
        );
    }
}
