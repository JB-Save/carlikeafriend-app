package com.carlikeafriend_backend.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)  // Mapea esta excepción a un código de estado HTTP 409 Conflict
public class UniqueProductException extends RuntimeException{
    public UniqueProductException(String message){
        super(message);
    }
}
