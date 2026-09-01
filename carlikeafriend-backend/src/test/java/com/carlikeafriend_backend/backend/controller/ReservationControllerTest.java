package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.*;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IJwtService;
import com.carlikeafriend_backend.backend.service.IReservationService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReservationController.class)
@WithMockUser
public class ReservationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private IReservationService reservationService;

    @MockitoBean
    private IJwtService jwtService;

    private UsernamePasswordAuthenticationToken createAuthUser() {
        User user = new User();
        user.setId(1L);
        return new UsernamePasswordAuthenticationToken(user, null, null);
    }

    @Test
    @DisplayName("POST /reservations - Crear reserva exitosamente")
    void createReservation_Success() throws Exception {
        ReservationDTO requestDTO = new ReservationDTO();
        requestDTO.setProductId(10L);
        requestDTO.setPickupBranchId(1L);
        requestDTO.setReturnBranchId(1L);
        requestDTO.setPickupDatetime(LocalDateTime.now().plusDays(1));
        requestDTO.setReturnDatetime(LocalDateTime.now().plusDays(3));
        requestDTO.setInsuranceType("BASIC");
        requestDTO.setUserTheMainDriver(Boolean.TRUE);

        UUID reservationId = UUID.randomUUID();
        ReservationResponseDTO responseDTO = new ReservationResponseDTO();
        responseDTO.setId(reservationId);
        responseDTO.setReservationStatus("PENDING_CONFIRMATION");

        when(reservationService.createReservation(eq(1L), any(ReservationDTO.class))).thenReturn(responseDTO);

        mockMvc.perform(post("/carlikeafriend/reservations")
                        .with(csrf())
                        .with(authentication(createAuthUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(reservationId.toString()))
                .andExpect(jsonPath("$.reservationStatus").value("PENDING_CONFIRMATION"));
    }

    @Test
    @DisplayName("PUT /reservations/{id}/start - Iniciar alquiler")
    void startRental_Success() throws Exception {
        UUID resId = UUID.randomUUID();
        doNothing().when(reservationService).startRental(resId, 1L);

        mockMvc.perform(put("/carlikeafriend/reservations/" + resId + "/start")
                        .with(csrf())
                        .with(authentication(createAuthUser())))
                .andExpect(status().isOk())
                .andExpect(content().string("Alquiler iniciado exitosamente. Vehículo en estado RENTED."));
    }

    @Test
    @DisplayName("PUT /reservations/{id}/complete - Completar alquiler")
    void completeRental_Success() throws Exception {
        UUID resId = UUID.randomUUID();
        doNothing().when(reservationService).completeRental(resId, 1L);

        mockMvc.perform(put("/carlikeafriend/reservations/" + resId + "/complete")
                        .with(csrf())
                        .with(authentication(createAuthUser())))
                .andExpect(status().isOk())
                .andExpect(content().string("Reserva completada exitosamente. Vehículo liberado y reubicado."));
    }

    @Test
    @DisplayName("GET /reservations/{id} - Buscar reserva por ID")
    void getReservationById_Success() throws Exception {
        UUID resId = UUID.randomUUID();
        ReservationResponseDTO dto = new ReservationResponseDTO();
        dto.setId(resId);

        when(reservationService.getReservationById(resId)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/carlikeafriend/reservations/" + resId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(resId.toString()));
    }

    @Test
    @DisplayName("GET /reservations/{productId}/blocked-dates - Consultar fechas bloqueadas")
    void getBlockedDates_Success() throws Exception {
        LocalDate start = LocalDate.now().plusDays(1);
        LocalDate end = LocalDate.now().plusDays(3);
        ResponseBlockedDatesDTO dto = new ResponseBlockedDatesDTO(List.of(start));

        when(reservationService.getBlockedDatesForProduct(10L, 1L, start, end)).thenReturn(dto);

        mockMvc.perform(get("/carlikeafriend/reservations/10/blocked-dates")
                        .param("branchId", "1")
                        .param("startDate", start.toString())
                        .param("endDate", end.toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.blockedDates[0]").value(start.toString()));
    }

    @Test
    @DisplayName("GET /reservations/me - Consultar mis reservas")
    void getMyReservations_Success() throws Exception {
        UserReservationResponseDTO dto = new UserReservationResponseDTO();
        dto.setId(UUID.randomUUID());

        when(reservationService.getUserReservations(1L, "upcoming")).thenReturn(List.of(dto));

        mockMvc.perform(get("/carlikeafriend/reservations/me")
                        .param("type", "upcoming")
                        .with(authentication(createAuthUser())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
    }

    @Test
    @DisplayName("PUT /reservations/{id}/cancel - Cancelar reserva")
    void cancelReservation_Success() throws Exception {
        UUID resId = UUID.randomUUID();
        Map<String, String> payload = Map.of("reason", "Cambio de planes");

        doNothing().when(reservationService).cancelReservation(resId, "Cambio de planes", 1L);

        mockMvc.perform(put("/carlikeafriend/reservations/" + resId + "/cancel")
                        .with(csrf())
                        .with(authentication(createAuthUser()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(payload)))
                .andExpect(status().isNoContent());
    }
}