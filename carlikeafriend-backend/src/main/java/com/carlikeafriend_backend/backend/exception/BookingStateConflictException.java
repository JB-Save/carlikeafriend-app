package com.carlikeafriend_backend.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookingStateConflictException extends RuntimeException {
    public BookingStateConflictException(String message) {
        super(message);
    }
}
