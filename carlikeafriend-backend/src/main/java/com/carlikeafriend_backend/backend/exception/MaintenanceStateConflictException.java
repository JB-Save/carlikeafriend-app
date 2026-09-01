package com.carlikeafriend_backend.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class MaintenanceStateConflictException extends RuntimeException {
    public MaintenanceStateConflictException(String message) {
        super(message);
    }
}
