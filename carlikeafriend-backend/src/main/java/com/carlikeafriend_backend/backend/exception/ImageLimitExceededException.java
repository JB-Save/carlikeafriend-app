package com.carlikeafriend_backend.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ImageLimitExceededException extends RuntimeException{ // Mapea esta excepción a un código de estado HTTP 400 Bad Request
    public ImageLimitExceededException(String message){
        super(message);
    }
}
