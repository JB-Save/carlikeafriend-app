package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.*;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.*;
import com.carlikeafriend_backend.backend.service.IRoleService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
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
    public SimpleResponseDTO saveRole(RoleDTO roleDTO) throws DuplicateResourceException {

        String roleName = StringUtils.normalizeToUpperCase(roleDTO.getName());

        logger.info("Intentando guardar nuevo rol: {}", roleName);

        // Validación de duplicados por nombre
        if (roleRepository.existsByNameAndDeletedFalse(roleName)) {
            logger.warn("Ya existe un rol activo con el nombre: {}", roleName);
            throw new DuplicateResourceException("Ya existe un rol activo con el nombre: " + roleName);
        }

        // Mapear DTO a Entidad
        Role role = new Role();
        role.setName(roleName);
        role.setDescription(roleDTO.getDescription());

        // Usar método auxiliar
        updatePermissions(role, roleDTO.getPermissions());

        Role savedRole = roleRepository.save(role);
        logger.info("Rol guardado exitosamente con ID: {}", savedRole.getId());
        return mapToRoleDto(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleCompleteResponseDTO> getAllRoles() {
        logger.info("Buscando todos los roles.");
        return roleRepository.findAllByDeletedFalse().stream()
                .map(this::mapToRoleCompleteDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<RoleCompleteResponseDTO> getRoleById(Long id) {
        logger.info("Buscando rol con ID: {}", id);
        return roleRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToRoleCompleteDto);
    }

    @Override
    @Transactional
    public SimpleResponseDTO updateRole(Long id, RoleDTO roleDTO) throws DuplicateResourceException {
        logger.info("Intentando actualizar rol con ID: {}", id);

        String roleName = StringUtils.normalizeToUpperCase(roleDTO.getName());

        Role existingRole = roleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Role no encontrado con ID: " + id));

        // Validar que el nombre del rol activo sea único, excluyendo el rol actual
        if (roleName != null && !roleName.equals(StringUtils.normalizeToUpperCase(existingRole.getName()))) {
            if (roleRepository.existsByNameAndIdNotAndDeletedFalse(roleName, id)) {
                throw new DuplicateResourceException("El nombre " + roleName + " ya está en uso por otro rol activo.");
            }
            existingRole.setName(roleName);
        }

        // Actualizar datos básicos del rol
        Optional.ofNullable(roleDTO.getDescription()).ifPresent(existingRole::setDescription);

        // Usar método auxiliar
        if (roleDTO.getPermissions() != null) {
            updatePermissions(existingRole, roleDTO.getPermissions());
        }

        Role updatedRole = roleRepository.save(existingRole);
        return mapToRoleDto(updatedRole);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado con ID: " + id));

        // Protección de roles del sistema
        if (role.isBaseRole()) {
            throw new DataIntegrityViolationException("Los roles principales del sistema (ADMIN/USER) no pueden ser eliminados.");
        }

        if (role.hasActiveUsers()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen usuarios activos asociados a este rol.");
        }

        // Si pasa ambas, procedemos al borrado lógico (Sufijo + deleted = true)
        String timestamp = String.valueOf(System.currentTimeMillis());
        role.setName(role.getName() + "_DELETED_" + timestamp);

        role.setDeleted(true);

        roleRepository.save(role);
        logger.info("Categoría con ID: {} borrada lógicamente.", id);
    }

    // MÉTODOS AUXILIARES

    private void updatePermissions(Role role, Set<Long> newIds) {
        // 1. Limpieza Bidireccional
        if (role.getPermissions() != null) {
            // Iterar sobre copia
            new ArrayList<>(role.getPermissions()).forEach(permission -> {
                permission.removeRole(role); // Método de conveniencia en Permission
            });
        }

        // 2. Asignación usando método de conveniencia de Permission
        if (newIds != null && !newIds.isEmpty()) {
            newIds.stream().filter(Objects::nonNull).forEach(id -> {
                Permission permission = permissionRepository.findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));
                permission.addRole(role); // Sincroniza ambos lados
            });
        }
    }

    private SimpleResponseDTO mapToRoleDto(Role role) {
        return new SimpleResponseDTO(role.getId(), role.getName());
    }

    private RoleCompleteResponseDTO mapToRoleCompleteDto(Role role) {
        List<SimpleResponseDTO> permissionDtos = new ArrayList<>();
        if (role.getPermissions() != null) {
            for (Permission permission : role.getPermissions()) {
                permissionDtos.add(new SimpleResponseDTO(permission.getId(), permission.getName()));
            }
        }
        return new RoleCompleteResponseDTO(role.getId(), role.getName(), role.getDescription(), permissionDtos);
    }
}
