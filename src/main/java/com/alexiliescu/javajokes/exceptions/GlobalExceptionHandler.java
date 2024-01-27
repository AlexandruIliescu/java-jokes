package com.alexiliescu.javajokes.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

import static org.springframework.http.HttpStatus.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MaxJokeCountExceededException.class)
    public ResponseEntity<Object> handleMaxJokeCountExceeded(MaxJokeCountExceededException exception) {
        log.error("MaxJokeCountExceeded: {}", exception.getMessage());
        return ResponseEntity.status(BAD_REQUEST).body(Map.of("message", exception.getMessage()));
    }

    @ExceptionHandler(NoJokesAvailableException.class)
    public ResponseEntity<Object> handleNoJokesAvailable(NoJokesAvailableException exception) {
        log.error("NoJokesAvailable: {}", exception.getMessage());
        return ResponseEntity.status(SERVICE_UNAVAILABLE).body(Map.of("message", exception.getMessage()));
    }
}