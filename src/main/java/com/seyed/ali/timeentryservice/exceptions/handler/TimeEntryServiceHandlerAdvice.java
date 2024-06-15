package com.seyed.ali.timeentryservice.exceptions.handler;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.seyed.ali.timeentryservice.exceptions.OperationNotSupportedException;
import com.seyed.ali.timeentryservice.exceptions.ResourceNotFoundException;
import com.seyed.ali.timeentryservice.model.payload.response.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.*;

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
        return ResponseEntity.status(NOT_ACCEPTABLE).body(new Result(
                false,
                NOT_ACCEPTABLE,
                "This operation is not supported.",
                "ServerMessage - " + e.getMessage()
        ));
    }

    @ExceptionHandler({ConnectException.class})
    public ResponseEntity<Result> handleConnectException(ConnectException e) {
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(new Result(
                false,
                SERVICE_UNAVAILABLE,
                "The service is not available 👎🏻",
                "ServerMessage 🚫 - " + e.getMessage()
        ));
    }

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<Result> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        List<ObjectError> allErrors = e.getBindingResult().getAllErrors();
        Map<String, String> map = new HashMap<>(allErrors.size());
        allErrors.forEach(objectError -> {
            String defaultMessage = objectError.getDefaultMessage();
            String field = ((FieldError) objectError).getField();
            map.put(field, defaultMessage);
        });

        return ResponseEntity.status(BAD_REQUEST).body(new Result(
                false,
                BAD_REQUEST,
                "Provided arguments are invalid, see data for details.",
                map
        ));
    }

    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ResponseEntity<Result> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        String errorMessage = "Invalid request format. Please check your request and try again.";
        Throwable cause = e.getCause();

        if (cause instanceof JsonParseException jsonParseException) {
            errorMessage = "JSON parse error: " + jsonParseException.getOriginalMessage();
        } else if (cause instanceof JsonMappingException jsonMappingException) {
            errorMessage = "JSON mapping error at " + jsonMappingException.getPathReference() + ": " + jsonMappingException.getOriginalMessage();
        }

        return ResponseEntity.status(BAD_REQUEST).body(new Result(
                false,
                BAD_REQUEST,
                errorMessage,
                e.getMessage()
        ));
    }

}
