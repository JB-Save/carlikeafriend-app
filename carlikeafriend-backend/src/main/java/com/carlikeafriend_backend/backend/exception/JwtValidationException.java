package com.carlikeafriend_backend.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta excepción se lanzará cuando falle la validación o el parsing del token JWT.
// Mapea a un código de estado HTTP 401 Unauthorized.
@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JwtValidationException extends RuntimeException{
    public JwtValidationException(String message){
        super(message);
    }

    public JwtValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}
