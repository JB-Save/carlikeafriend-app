package com.carlikeafriend_backend.backend.service.impl;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.Permission;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.IPermissionRepository;
import com.carlikeafriend_backend.backend.service.IPermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public PermissionResponseDTO savePermission(PermissionDTO permissionDTO) throws UniqueNameException {

        logger.info("Intentando guardar nuevo permiso: {}", permissionDTO.getName());

        // Validación de duplicados por nombre
        if (permissionRepository.existsByName(permissionDTO.getName())) {
            logger.warn("El nombre del permiso ya existe: {}", permissionDTO.getName());
            throw new UniqueNameException("El nombre del permiso ya existe.");
        }

        // Mapear DTO a Entidad
        Permission permission = new Permission();
        permission.setName(permissionDTO.getName());
        permission.setDescription(permissionDTO.getDescription());

        Permission savedPermission = permissionRepository.save(permission);
        logger.info("Permiso guardado exitosamente con ID: {}", savedPermission.getId());
        return convertToDto(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponseCompleteDTO> findAllPermissions() {
        logger.info("Buscando todos los permisos.");
        return permissionRepository.findAll().stream()
                .map(this::convertToDtoResponseFoundByIdDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<PermissionResponseCompleteDTO> findPermissionById(Long id) {
        logger.info("Buscando permiso con ID: {}", id);
        return permissionRepository.findById(id)
                .map(this::convertToDtoResponseFoundByIdDTO);
    }

    @Override
    @Transactional
    public PermissionResponseDTO updatePermission(Long id, PermissionDTO permissionDTO) throws UniqueNameException {

        logger.info("Intentando actualizar permiso con ID: {}", id);

       Permission existingPermission = permissionRepository.findById(id)
               .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));


             // Validar que el nombre del permiso sea único, excluyendo el permiso actual
            if (permissionDTO.getName() != null && !permissionDTO.getName().equals(existingPermission.getName()) && permissionRepository.existsByNameAndIdNot(permissionDTO.getName(), id)) {
                throw new UniqueNameException("El nombre del permiso ya existe.");
            }

            // Actualizar datos básicos del permiso
            Optional.ofNullable(permissionDTO.getName()).ifPresent(existingPermission::setName);
            Optional.ofNullable(permissionDTO.getDescription()).ifPresent(existingPermission::setDescription);

            Permission updatedPermission = permissionRepository.save(existingPermission);
            return convertToDto(updatedPermission);

    }

    @Override
    @Transactional
    public void deletePermission(Long id) {
        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Permiso no encontrado con ID: " + id));

        permissionRepository.delete(permission);
        logger.warn("Permiso eliminado con ID: {}", id);
    }


    private PermissionResponseDTO convertToDto(Permission permission) {
        return new PermissionResponseDTO(
                permission.getId(),
                permission.getName()
        );
    }

    private PermissionResponseCompleteDTO convertToDtoResponseFoundByIdDTO(Permission permission) {
        return new PermissionResponseCompleteDTO(
                permission.getId(),
                permission.getName(),
                permission.getDescription()
        );
    }
}
