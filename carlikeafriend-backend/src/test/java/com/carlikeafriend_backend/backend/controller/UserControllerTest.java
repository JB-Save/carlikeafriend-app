package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.service.IUserService;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado globalmente para la clase
public class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private IJwtService jwtService; // Requerido para el contexto de seguridad si se usa JWT

    @Test
    @DisplayName("GET /users - Debería retornar lista de usuarios")
    void findAllUsers_Success() throws Exception {
        UserResponseCompleteDTO user = new UserResponseCompleteDTO(1L, "Juan", "Perez", "juan@test.com", List.of());
        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(get("/carlikeafriend/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("juan@test.com"));
    }

    @Test
    @DisplayName("GET /users/{id} - Debería retornar usuario por ID")
    void findById_Success() throws Exception {
        UserResponseCompleteDTO user = new UserResponseCompleteDTO(1L, "Juan", "Perez", "juan@test.com", List.of());
        when(userService.findById(1L)).thenReturn(Optional.of(user));

        mockMvc.perform(get("/carlikeafriend/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    @Test
    @DisplayName("PUT /users/{id} - Debería actualizar usuario exitosamente")
    void updateUser_Success() throws Exception {
        UserDTO updateDto = new UserDTO("Juan", "Actualizado", "juan@test.com", Set.of(1L));
        UserResponseDTO responseDto = new UserResponseDTO(1L, "Juan", "Actualizado", "juan@test.com");

        when(userService.updateUserFromAdmin(eq(1L), any(UserDTO.class))).thenReturn(responseDto);

        mockMvc.perform(put("/carlikeafriend/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Actualizado"));
    }

    @Test
    @DisplayName("DELETE /users/{id} - Debería retornar 204")
    void deleteUser_Success() throws Exception {
        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/carlikeafriend/users/1").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("POST /email/resend-confirmation - Debería retornar mensaje genérico")
    void resendConfirmationEmail_Success() throws Exception {
        Map<String, String> request = Map.of("email", "test@test.com");

        mockMvc.perform(post("/carlikeafriend/users/email/resend-confirmation")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("recibirá un correo electrónico")));

        verify(userService).resendConfirmationEmail(eq("test@test.com"), any());
    }
}