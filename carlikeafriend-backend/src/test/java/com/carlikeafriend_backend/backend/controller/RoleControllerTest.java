package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.RoleCompleteResponseDTO;
import com.carlikeafriend_backend.backend.dto.RoleDTO;
import com.carlikeafriend_backend.backend.dto.SimpleResponseDTO;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.carlikeafriend_backend.backend.service.IRoleService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RoleController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado globalmente para la clase
class RoleControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IRoleService roleService;

    @Autowired
    private ObjectMapper objectMapper;

    // --- Mocks necesarios para que el filtro de seguridad JWT no falle ---
    @MockitoBean
    private IJwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private RoleDTO roleDTO;
    private SimpleResponseDTO roleResponseDTO;
    private RoleCompleteResponseDTO roleCompleteResponseDTO;

    @BeforeEach
    void setUp() {
        roleDTO = new RoleDTO();
        roleDTO.setName("EDITOR");
        roleDTO.setDescription("Puede editar contenido");

        roleResponseDTO = new SimpleResponseDTO(1L, "EDITOR");
        roleCompleteResponseDTO = new RoleCompleteResponseDTO(1L, "EDITOR", "Desc", List.of());
    }

    @Test
    @DisplayName("POST /roles - Debería crear un rol exitosamente")
    void testSaveRole_StatusCreated() throws Exception {
        when(roleService.saveRole(any(RoleDTO.class))).thenReturn(roleResponseDTO);

        mockMvc.perform(post("/carlikeafriend/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("EDITOR"));
    }

    @Test
    @DisplayName("POST /roles - Retornar 409 Conflict si el rol ya existe")
    void testSaveRole_DuplicateName_StatusConflict() throws Exception {
        when(roleService.saveRole(any(RoleDTO.class)))
                .thenThrow(new DuplicateResourceException("Ya existe un rol activo"));

        mockMvc.perform(post("/carlikeafriend/roles")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /roles - Debería retornar lista de roles")
    void testFindAllRoles_StatusOK() throws Exception {
        when(roleService.getAllRoles()).thenReturn(List.of(roleCompleteResponseDTO));

        mockMvc.perform(get("/carlikeafriend/roles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("EDITOR"));
    }

    @Test
    @DisplayName("GET /roles/{id} - Retornar 404 si no existe")
    void testFindRoleById_StatusNotFound() throws Exception {
        when(roleService.getRoleById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/carlikeafriend/roles/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /roles/{id} - Debería actualizar el rol")
    void testUpdateRole_StatusOK() throws Exception {
        when(roleService.updateRole(eq(1L), any(RoleDTO.class))).thenReturn(roleResponseDTO);

        mockMvc.perform(put("/carlikeafriend/roles/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /roles/{id} - Retornar 204")
    void testDeleteRole_StatusNoContent() throws Exception {
        mockMvc.perform(delete("/carlikeafriend/roles/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}