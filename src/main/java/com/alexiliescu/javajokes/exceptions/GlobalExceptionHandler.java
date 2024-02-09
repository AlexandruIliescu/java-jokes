package com.alexiliescu.javajokes.exceptions;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoJokesAvailableException.class)
    public ResponseEntity<Object> handleNoJokesAvailable(NoJokesAvailableException exception) {
        log.error("NoJokesAvailable: {}", exception.getMessage());
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex, WebRequest request) {
        Map<String, String> errors = new LinkedHashMap<>();
        String message = "";
        for (ConstraintViolation<?> violation : ex.getConstraintViolations()) {
            String propertyPath = violation.getPropertyPath().toString();
            String fieldName = propertyPath.substring(propertyPath.lastIndexOf('.') + 1);
            message = violation.getMessage();
            errors.put(fieldName, message);
        }
        log.error("ConstraintViolation: {}", message);
        return ResponseEntity.status(BAD_REQUEST).body(errors);
    }
}