package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.PermissionDTO;
import com.carlikeafriend_backend.backend.dto.PermissionResponseCompleteDTO;
import com.carlikeafriend_backend.backend.dto.PermissionResponseDTO;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.carlikeafriend_backend.backend.service.IPermissionService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PermissionController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"})
class PermissionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private IPermissionService permissionService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IJwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    private PermissionDTO permissionDTO;
    private PermissionResponseDTO permissionResponseDTO;

    @BeforeEach
    void setUp() {
        permissionDTO = new PermissionDTO();
        permissionDTO.setName("UPDATE_PRODUCT");
        permissionDTO.setDescription("Permite actualizar productos");

        permissionResponseDTO = new PermissionResponseDTO(1L, "UPDATE_PRODUCT");
    }

    @Test
    @DisplayName("POST /permissions - Debería crear un permiso exitosamente")
    void testSavePermission_StatusCreated() throws Exception {
        when(permissionService.savePermission(any(PermissionDTO.class))).thenReturn(permissionResponseDTO);

        mockMvc.perform(post("/carlikeafriend/permissions")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("UPDATE_PRODUCT"));
    }

    @Test
    @DisplayName("GET /permissions - Debería retornar lista de permisos")
    void testFindAllPermissions_StatusOK() throws Exception {
        PermissionResponseCompleteDTO completeDTO = new PermissionResponseCompleteDTO(1L, "UPDATE_PRODUCT", "Desc");
        when(permissionService.findAllPermissions()).thenReturn(List.of(completeDTO));

        mockMvc.perform(get("/carlikeafriend/permissions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("UPDATE_PRODUCT"));
    }

    @Test
    @DisplayName("GET /permissions/{id} - Retornar 404 si no existe")
    void testFindPermissionById_StatusNotFound() throws Exception {
        when(permissionService.findPermissionById(99L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/carlikeafriend/permissions/99"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /permissions/{id} - Debería actualizar el permiso")
    void testUpdatePermission_StatusOK() throws Exception {
        when(permissionService.updatePermission(eq(1L), any(PermissionDTO.class))).thenReturn(permissionResponseDTO);

        mockMvc.perform(put("/carlikeafriend/permissions/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(permissionDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    @DisplayName("DELETE /permissions/{id} - Retornar 204")
    void testDeletePermission_StatusNoContent() throws Exception {
        mockMvc.perform(delete("/carlikeafriend/permissions/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }
}