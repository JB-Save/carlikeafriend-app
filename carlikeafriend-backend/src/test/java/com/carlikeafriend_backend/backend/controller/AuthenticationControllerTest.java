package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IUserService;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashSet;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthenticationController.class)
@WithMockUser(username = "admin", roles = {"ADMIN"}) // Simula usuario autenticado globalmente para la clase
public class AuthenticationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IUserService userService;

    @MockitoBean
    private IJwtService jwtService;

    @Test
    @DisplayName("POST /register - Debería registrar un usuario exitosamente")
    void register_Success() throws Exception {
        RegisterUserDTO registerDTO = new RegisterUserDTO("Juan", "Perez", "juan@test.com", "Password123!", new HashSet<>());
        UserResponseDTO responseDTO = new UserResponseDTO(1L, "Juan", "Perez", "juan@test.com");

        when(userService.registerUser(any(RegisterUserDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/carlikeafriend/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("juan@test.com"));
    }

    @Test
    @DisplayName("POST /login - Debería retornar token y datos de usuario")
    void login_Success() throws Exception {
        UserAuthenticationDTO loginDTO = new UserAuthenticationDTO("juan@test.com", "Password123!");
        UserAuthenticationResponseDTO responseDTO = new UserAuthenticationResponseDTO("fake-jwt-token", 1L, "Juan", "Perez", "juan@test.com", List.of());

        when(userService.login(any(UserAuthenticationDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/carlikeafriend/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("fake-jwt-token"))
                .andExpect(jsonPath("$.userName").value("juan@test.com"));
    }

    @Test
    @WithMockUser
    @DisplayName("GET /me - Debería retornar el perfil del usuario autenticado")
    void getAuthenticatedUserProfile_Success() throws Exception {
        // Mock de la entidad User que el filtro de seguridad pondría en el contexto
        User mockUser = new User();
        mockUser.setId(1L);
        mockUser.setName("Juan");
        mockUser.setLastName("Perez");
        mockUser.setEmail("juan@test.com");
        mockUser.setRoles(new HashSet<>());

        // Inyectamos manualmente en el contexto de seguridad para el test
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(mockUser, null, List.of());
        SecurityContextHolder.getContext().setAuthentication(auth);

        mockMvc.perform(get("/carlikeafriend/auth/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("juan@test.com"))
                .andExpect(jsonPath("$.name").value("Juan"));
    }
}