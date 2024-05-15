package com.seyed.ali.timeentryservice.exceptions;

import lombok.Getter;

@Getter
@SuppressWarnings("unused")
public class OperationNotSupportedException extends RuntimeException {

    private String message;

    public OperationNotSupportedException() {
    }

    public OperationNotSupportedException(String message) {
        super(message);
        this.message = message;
    }

}
