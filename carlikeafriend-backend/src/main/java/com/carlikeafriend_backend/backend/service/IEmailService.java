package com.carlikeafriend_backend.backend.service;

public interface IEmailService {
    void sendRegistrationConfirmation(String userEmail, String username, String loginUrl);
}
