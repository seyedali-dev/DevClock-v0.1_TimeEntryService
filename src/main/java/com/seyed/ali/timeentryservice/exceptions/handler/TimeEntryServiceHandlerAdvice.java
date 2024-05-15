package com.seyed.ali.timeentryservice.exceptions.handler;

import com.seyed.ali.timeentryservice.exceptions.OperationNotSupportedException;
import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.dto.response.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@RestControllerAdvice
public class TimeEntryServiceHandlerAdvice {

    @ExceptionHandler({AccessDeniedException.class})
    public ResponseEntity<Result> handleAccessDeniedException(AccessDeniedException e) {
        return ResponseEntity.status(FORBIDDEN).body(new Result(
                false,
                FORBIDDEN,
                "No permission.",
                "ServerMessage - " + e.getMessage()
        ));
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<Result> handleResourceNotFoundException(ResourceNotFoundException e) {
        return ResponseEntity.status(NOT_FOUND).body(new Result(
                false,
                NOT_FOUND,
                "The requested resource was not found.",
                "ServerMessage - " + e.getMessage()
        ));
    }

    @ExceptionHandler({OperationNotSupportedException.class})
    public ResponseEntity<Result> handleOperationNotSupportedException(OperationNotSupportedException e) {
        return ResponseEntity.status(NOT_FOUND).body(new Result(
                false,
                NOT_FOUND,
                "This operation is not supported.",
                "ServerMessage - " + e.getMessage()
        ));
    }

}
