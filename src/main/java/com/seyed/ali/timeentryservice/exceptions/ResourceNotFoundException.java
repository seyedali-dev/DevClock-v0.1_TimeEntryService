package com.seyed.ali.timeentryservice.exceptions;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@SuppressWarnings("unused")
public class ResourceNotFoundException extends RuntimeException {

    private String message;
    private HttpStatus httpStatus;

    public ResourceNotFoundException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public ResourceNotFoundException(String message) {
        super(message);
        this.message = message;
    }

    public ResourceNotFoundException() {
    }

}
