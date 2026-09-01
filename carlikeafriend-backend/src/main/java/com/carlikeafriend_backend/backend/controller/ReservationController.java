package com.carlikeafriend_backend.backend.controller;

import com.carlikeafriend_backend.backend.dto.ReservationDTO;
import com.carlikeafriend_backend.backend.dto.ReservationResponseDTO;
import com.carlikeafriend_backend.backend.dto.ResponseBlockedDatesDTO;
import com.carlikeafriend_backend.backend.dto.UserReservationResponseDTO;
import com.carlikeafriend_backend.backend.entity.User;
import com.carlikeafriend_backend.backend.service.IReservationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/carlikeafriend")
public class ReservationController {

    private final IReservationService reservationService;

    @Autowired
    public ReservationController(IReservationService reservationService) {
        this.reservationService = reservationService;
    }

    @PostMapping("/reservations")
    public ResponseEntity<ReservationResponseDTO> createReservation(
            @RequestBody @Valid ReservationDTO request,
            @AuthenticationPrincipal User currentUser) {

        ReservationResponseDTO createdReservation = reservationService.createReservation(currentUser.getId(), request);
        return new ResponseEntity<>(createdReservation, HttpStatus.CREATED);
    }

    @PutMapping("/reservations/{id}/start")
    public ResponseEntity<String> startRental(@PathVariable UUID id,
                                              @AuthenticationPrincipal User currentUser) {
        reservationService.startRental(id, currentUser.getId());
        return new ResponseEntity<>("Alquiler iniciado exitosamente. Vehículo en estado RENTED.", HttpStatus.OK);
    }

    @PutMapping("/reservations/{id}/complete")
    public ResponseEntity<String> completeRental(@PathVariable UUID id,
                                                 @AuthenticationPrincipal User currentUser) {
        reservationService.completeRental(id, currentUser.getId());
        return new ResponseEntity<>("Reserva completada exitosamente. Vehículo liberado y reubicado.", HttpStatus.OK);
    }

    @GetMapping("/reservations/{id}")
    public ResponseEntity<ReservationResponseDTO> getReservationById(@PathVariable UUID id) {
        Optional<ReservationResponseDTO> reservationResponseDTO = reservationService.getReservationById(id);
        return reservationResponseDTO.map(dto -> new ResponseEntity<>(dto, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/reservations/{productId}/blocked-dates")
    public ResponseEntity<ResponseBlockedDatesDTO> getBlockedDates(
            @PathVariable Long productId,
            @RequestParam Long branchId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        ResponseBlockedDatesDTO blockedDates = reservationService.getBlockedDatesForProduct(productId, branchId, startDate, endDate);
        return new ResponseEntity<>(blockedDates, HttpStatus.OK);
    }

    @GetMapping("/reservations/me")
    public ResponseEntity<List<UserReservationResponseDTO>> getMyReservations(
            @RequestParam(defaultValue = "upcoming") String type,
            @AuthenticationPrincipal User currentUser) {

        List<UserReservationResponseDTO> userReservations = reservationService.getUserReservations(
                currentUser.getId(),
                type
        );

        return new ResponseEntity<>(userReservations, HttpStatus.OK);
    }

    @PutMapping("/reservations/{id}/cancel")
    public ResponseEntity<Void> cancelReservation(
            @PathVariable UUID id,
            @RequestBody Map<String, String> payload,
            @AuthenticationPrincipal User currentUser) {

        String reason = payload.getOrDefault("reason", "Cancelación por el usuario");

        reservationService.cancelReservation(id, reason, currentUser.getId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
