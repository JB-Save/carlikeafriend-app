package com.carlikeafriend_backend.backend.service;

import com.carlikeafriend_backend.backend.entity.Reservation;

public interface IEmailService {
    void sendRegistrationConfirmation(String userEmail, String username, String loginUrl);
    void sendReservationConfirmation(Reservation reservation);
}
