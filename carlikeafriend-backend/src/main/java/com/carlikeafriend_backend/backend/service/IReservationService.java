package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.dto.ReservationDTO;
import com.carlikeafriend_backend.backend.dto.ReservationResponseDTO;
import com.carlikeafriend_backend.backend.dto.ResponseBlockedDatesDTO;
import com.carlikeafriend_backend.backend.dto.UserReservationResponseDTO;
import org.springframework.data.domain.Sort;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface IReservationService {
    ReservationResponseDTO createReservation(Long userId, ReservationDTO request);

    Optional<ReservationResponseDTO> getReservationById(UUID id);

    ResponseBlockedDatesDTO getBlockedDatesForProduct(Long productId, Long branchId, LocalDate startDate, LocalDate endDate);

    List<UserReservationResponseDTO> getUserReservations(Long userId, String type);

    void startRental(UUID id, Long employeeId);

    void completeRental(UUID id, Long employeeId);

    void cancelReservation(UUID id, String reason, Long userId);

    void cancelReservationBySystem(UUID id, String reason);


}
