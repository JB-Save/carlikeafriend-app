package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.exception.DuplicateResourceException;
import com.carlikeafriend_backend.backend.service.IUserService;
import com.carlikeafriend_backend.backend.service.IJwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import com.carlikeafriend_backend.backend.entity.User;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
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

    private UserDTO updateDto;
    private UserProfileDTO profileDto;
    private UserCompleteResponseDTO completeUser;

    @BeforeEach
    void setUp() {
        completeUser = new UserCompleteResponseDTO(1L, "Juan", "Perez", "juan@test.com", List.of());

        updateDto = new UserDTO();
        updateDto.setName("Juan");
        updateDto.setLastName("Actualizado");
        updateDto.setDocumentType("CC");
        updateDto.setDocumentNumber("1111111");
        updateDto.setPhoneNumber("+57310333333");
        updateDto.setNationality("Colombiano");
        updateDto.setCountryCode("CO");
        updateDto.setStateCode("ANT");
        updateDto.setCity("Medellín");
        updateDto.setAddress("Calle 29 No,32-25");
        updateDto.setZipCode("50001");
        updateDto.setBirthDate(LocalDate.of(2005, 1, 12));
        updateDto.setDriverLicenseNumber("1111111");
        updateDto.setDriverLicenseExpiry(LocalDate.of(2030, 12, 31));
        updateDto.setEmergencyContactName("Lisa Dominguez");
        updateDto.setEmergencyContactPhone("+57310333334");
        updateDto.setEmail("juan@test.com");
        updateDto.setRoleIds(Set.of(1L));

        profileDto = new UserProfileDTO();
        profileDto.setName("Juan");
        profileDto.setLastName("Actualizado");
        profileDto.setDocumentType("CC");
        profileDto.setDocumentNumber("1111111");
        profileDto.setPhoneNumber("+57310333333");
        profileDto.setNationality("Colombiano");
        profileDto.setCountryCode("CO");
        profileDto.setStateCode("ANT");
        profileDto.setCity("Medellín");
        profileDto.setAddress("Calle 29 No,32-25");
        profileDto.setZipCode("50001");
        profileDto.setBirthDate(LocalDate.of(2005, 1, 12));
        profileDto.setDriverLicenseNumber("1111111");
        profileDto.setDriverLicenseExpiry(LocalDate.of(2030, 12, 31));
        profileDto.setEmergencyContactName("Lisa Dominguez");
        profileDto.setEmergencyContactPhone("+57310333334");
        profileDto.setEmail("juan@test.com");
    }

    @Test
    @DisplayName("GET /users - Debería retornar lista de usuarios")
    void findAllUsers_Success() throws Exception {
        when(userService.getAllUsers()).thenReturn(List.of(completeUser));

        mockMvc.perform(get("/carlikeafriend/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("juan@test.com"));
    }

    @Test
    @DisplayName("GET /users/{id} - Debería retornar usuario por ID")
    void findById_Success() throws Exception {
        when(userService.getUserById(1L)).thenReturn(Optional.of(completeUser));

        mockMvc.perform(get("/carlikeafriend/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    @Test
    @DisplayName("PUT /users/{id} - Debería actualizar usuario exitosamente")
    void updateUser_Success() throws Exception {
        UserAuthenticationResponseDTO responseDto = new UserAuthenticationResponseDTO("eyJhbGciOiJIUz", 1L, "Juan", "Actualizado", "juan@test.com", List.of(new SimpleResponseDTO(1L, "USER")));

        when(userService.updateUserFromAdmin(eq(1L), any(UserDTO.class))).thenReturn(responseDto);

        mockMvc.perform(put("/carlikeafriend/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.lastName").value("Actualizado"));
    }

    @Test
    @DisplayName("PUT /users/{id} - Error 409 por email duplicado")
    void updateUser_Conflict() throws Exception {
        when(userService.updateUserFromAdmin(eq(1L), any(UserDTO.class)))
                .thenThrow(new DuplicateResourceException("Email duplicado"));

        mockMvc.perform(put("/carlikeafriend/users/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("GET /users/account - Debería retornar el perfil del usuario actual")
    void getUserProfile_Success() throws Exception {
        User customUser = new User();
        customUser.setId(1L);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, null);

        UserProfileResponseDTO profile = new UserProfileResponseDTO();
        profile.setName("Juan");
        when(userService.getUserProfileById(any())).thenReturn(Optional.of(profile));

        mockMvc.perform(get("/carlikeafriend/users/account")
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    @Test
    @DisplayName("PUT /users/account - Debería actualizar el perfil del usuario actual")
    void updateMyProfile_Success() throws Exception {
        User customUser = new User();
        customUser.setId(1L);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, null);

        UserResponseDTO response = new UserResponseDTO(1L, "Juan", "Perez", "juan@test.com");
        when(userService.updateUserProfile(any(), any(UserProfileDTO.class))).thenReturn(response);

        mockMvc.perform(put("/carlikeafriend/users/account")
                        .with(csrf())
                        .with(authentication(auth))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(profileDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Juan"));
    }

    @Test
    @DisplayName("DELETE /users/account/me/deactivate - Debería desactivar cuenta")
    void deactivateMyAccount_Success() throws Exception {
        User customUser = new User();
        customUser.setId(1L);
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(customUser, null, null);

        doNothing().when(userService).deleteUser(1L);

        mockMvc.perform(delete("/carlikeafriend/users/account/me/deactivate")
                        .with(csrf())
                        .with(authentication(auth)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").exists());
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