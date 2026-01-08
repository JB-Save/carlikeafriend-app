package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.PermissionDTO;
import com.carlikeafriend_backend.backend.dto.PermissionResponseCompleteDTO;
import com.carlikeafriend_backend.backend.dto.PermissionResponseDTO;
import com.carlikeafriend_backend.backend.entity.Permission;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.repository.IPermissionRepository;
import com.carlikeafriend_backend.backend.service.impl.PermissionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private IPermissionRepository permissionRepository;

    @InjectMocks
    private PermissionService permissionService;

    private Permission permission;
    private PermissionDTO permissionDTO;

    @BeforeEach
    void setUp() {
        permission = new Permission();
        permission.setId(1L);
        permission.setName("CREATE_PRODUCT");
        permission.setDescription("Permite crear nuevos productos");

        permissionDTO = new PermissionDTO();
        permissionDTO.setName("CREATE_PRODUCT");
        permissionDTO.setDescription("Permite crear nuevos productos");
    }

    @Test
    @DisplayName("Crear Permiso - Éxito")
    void testSavePermission_Success() {
        when(permissionRepository.existsByName(anyString())).thenReturn(false);
        when(permissionRepository.save(any(Permission.class))).thenReturn(permission);

        PermissionResponseDTO result = permissionService.savePermission(permissionDTO);

        assertNotNull(result);
        assertEquals("CREATE_PRODUCT", result.getName());
        verify(permissionRepository).save(any(Permission.class));
    }

    @Test
    @DisplayName("Crear Permiso - Error si el nombre ya existe")
    void testSavePermission_ThrowsUniqueNameException() {
        when(permissionRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(UniqueNameException.class, () -> permissionService.savePermission(permissionDTO));
        verify(permissionRepository, never()).save(any(Permission.class));
    }

    @Test
    @DisplayName("Obtener permiso por ID - Éxito")
    void testFindPermissionById_Success() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        Optional<PermissionResponseCompleteDTO> result = permissionService.findPermissionById(1L);

        assertTrue(result.isPresent());
        assertEquals("CREATE_PRODUCT", result.get().getName());
    }

    @Test
    @DisplayName("Eliminar Permiso - Éxito")
    void testDeletePermission_Success() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        assertDoesNotThrow(() -> permissionService.deletePermission(1L));
        verify(permissionRepository).delete(permission);
    }

    @Test
    @DisplayName("Eliminar Permiso - Error si no existe")
    void testDeletePermission_ThrowsResourceNotFoundException() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> permissionService.deletePermission(1L));
    }
}