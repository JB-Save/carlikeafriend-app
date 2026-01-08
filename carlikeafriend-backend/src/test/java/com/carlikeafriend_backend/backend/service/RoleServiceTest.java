package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.RoleDTO;
import com.carlikeafriend_backend.backend.dto.RoleResponseCompleteDTO;
import com.carlikeafriend_backend.backend.dto.RoleResponseDTO;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.UniqueNameException;
import com.carlikeafriend_backend.backend.entity.Role;
import com.carlikeafriend_backend.backend.repository.IRoleRepository;
import com.carlikeafriend_backend.backend.service.impl.RoleService;
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
class RoleServiceTest {

    @Mock
    private IRoleRepository roleRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleDTO roleDTO;

    @BeforeEach
    void setUp() {
        role = new Role();
        role.setId(1L);
        role.setName("ADMIN");
        role.setDescription("Administrador del sistema");

        roleDTO = new RoleDTO();
        roleDTO.setName("ADMIN");
        roleDTO.setDescription("Administrador del sistema");
    }

    @Test
    @DisplayName("Crear Rol - Éxito")
    void testSaveRole_Success() {
        when(roleRepository.existsByName(anyString())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        RoleResponseDTO result = roleService.saveRole(roleDTO);

        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    @DisplayName("Crear Role - Error si el nombre del rol ya existe")
    void testSaveRole_ThrowsUniqueNameException() {
        when(roleRepository.existsByName(anyString())).thenReturn(true);

        assertThrows(UniqueNameException.class, () -> roleService.saveRole(roleDTO));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("Obtener rol por ID - Éxito")
    void testFindRoleById_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        Optional<RoleResponseCompleteDTO> result = roleService.findRoleById(1L);

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
    }

    @Test
    @DisplayName("Eliminar Producto - Éxito")
    void testDeleteRole_Success() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        assertDoesNotThrow(() -> roleService.deleteRole(1L));
    }

    @Test
    void testDeleteRole_ThrowsResourceNotFoundException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRole(1L));
    }
}