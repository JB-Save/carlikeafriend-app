package com.carlikeafriend_backend.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class InvalidResourceStateException extends RuntimeException{
    public InvalidResourceStateException(String message){
        super(message);
    }
}
