package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.RoleDTO;
import com.carlikeafriend_backend.backend.dto.RoleCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.ResourceNotFoundException;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
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
import org.springframework.dao.DataIntegrityViolationException;

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
        when(roleRepository.existsByNameAndDeletedFalse(anyString())).thenReturn(false);
        when(roleRepository.save(any(Role.class))).thenReturn(role);

        SimpleResponseDTO result = roleService.saveRole(roleDTO);

        assertNotNull(result);
        assertEquals("ADMIN", result.getName());
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    @DisplayName("Crear Role - Error si el nombre del rol ya existe")
    void testSaveRole_ThrowsUniqueNameException() {
        when(roleRepository.existsByNameAndDeletedFalse(anyString())).thenReturn(true);

        assertThrows(DuplicateResourceException.class, () -> roleService.saveRole(roleDTO));
        verify(roleRepository, never()).save(any(Role.class));
    }

    @Test
    @DisplayName("Obtener rol por ID - Éxito")
    void testFindRoleById_Success() {
        when(roleRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(role));

        Optional<RoleCompleteResponseDTO> result = roleService.getRoleById(1L);

        assertTrue(result.isPresent());
        assertEquals("ADMIN", result.get().getName());
    }

    @Test
    @DisplayName("Eliminar único usuario ADMIN - Excepción: Violación de integridad")
    void testDeleteRole_Success() {
        when(roleRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(role));

        assertThrows(DataIntegrityViolationException.class, () -> roleService.deleteRole(1L));
        //assertDoesNotThrow(() -> roleService.deleteRole(1L));
    }

    @Test
    @DisplayName("Eliminar Rol - Excepción: Rol no encontrado")
    void testDeleteRole_ThrowsResourceNotFoundException() {
        when(roleRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> roleService.deleteRole(1L));
    }
}