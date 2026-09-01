package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.Permission;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.repository.IPermissionRepository;
import com.carlikeafriend_backend.backend.service.IPermissionService;
import com.carlikeafriend_backend.backend.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PermissionService implements IPermissionService {

    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    private final IPermissionRepository permissionRepository;

    @Autowired
    public PermissionService(IPermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Override
    @Transactional
    public SimpleResponseDTO savePermission(PermissionDTO permissionDTO) throws DuplicateResourceException {

        String permissionName = StringUtils.normalizeToUpperCase(permissionDTO.getName());

        logger.info("Intentando guardar nuevo permiso: {}", permissionName);

        // Validación de duplicados por nombre
        if (permissionRepository.existsByNameAndDeletedFalse(permissionName)) {
            logger.warn("Ya existe un permiso activo con el nombre: {}", permissionName);
            throw new DuplicateResourceException("Ya existe un permiso activo con el nombre: " + permissionName);
        }

        // Mapear DTO a Entidad
        Permission permission = new Permission();
        permission.setName(permissionName);
        permission.setDescription(permissionDTO.getDescription());

        Permission savedPermission = permissionRepository.save(permission);
        logger.info("Permiso guardado exitosamente con ID: {}", savedPermission.getId());
        return mapToPermissionDto(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionCompleteResponseDTO> getAllPermissions() {
        logger.info("Buscando todos los permisos.");
        return permissionRepository.findAllByDeletedFalse().stream()
                .map(this::mapToPermissionCompleteDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PermissionCompleteResponseDTO> getPermissionById(Long id) {
        logger.info("Buscando permiso con ID: {}", id);
        return permissionRepository.findByIdAndDeletedFalse(id)
                .map(this::mapToPermissionCompleteDto);
    }

    @Override
    @Transactional
    public SimpleResponseDTO updatePermission(Long id, PermissionDTO permissionDTO) throws DuplicateResourceException {

        logger.info("Intentando actualizar permiso con ID: {}", id);

        String permissionName = StringUtils.normalizeToUpperCase(permissionDTO.getName());

        Permission existingPermission = permissionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("No se puede actualizar: Permiso no encontrado con ID: " + id));


        // Validar que el nombre del permiso activo sea único, excluyendo el permiso actual
        if (permissionName != null && !permissionName.equals(StringUtils.normalizeToUpperCase(existingPermission.getName()))) {
            if (permissionRepository.existsByNameAndIdNotAndDeletedFalse(permissionName, id)) {
                throw new DuplicateResourceException("El nombre " + permissionName + " ya está en uso por otro permiso activo.");
            }
            existingPermission.setName(permissionName);
        }

        // Actualizar datos básicos del permiso
        Optional.ofNullable(permissionDTO.getDescription()).ifPresent(existingPermission::setDescription);

        Permission updatedPermission = permissionRepository.save(existingPermission);
        return mapToPermissionDto(updatedPermission);

    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));

        if (permission.hasActiveRoles()) {
            throw new DataIntegrityViolationException("No se puede eliminar: Existen roles activos asociados a este permiso.");
        }

        // Si pasa, procedemos al borrado lógico (Sufijo + deleted = true)
        String timestamp = String.valueOf(System.currentTimeMillis());
        permission.setName(permission.getName() + "_DELETED_" + timestamp);

        permission.setDeleted(true);

        permissionRepository.save(permission);
        logger.info("Permiso con ID: {} borrado lógicamente.", id);

    }

    private SimpleResponseDTO mapToPermissionDto(Permission permission) {
        return new SimpleResponseDTO(
                permission.getId(),
                permission.getName()
        );
    }

    private PermissionCompleteResponseDTO mapToPermissionCompleteDto(Permission permission) {
        return new PermissionCompleteResponseDTO(
                permission.getId(),
                permission.getName(),
                permission.getDescription()
        );
    }
}
