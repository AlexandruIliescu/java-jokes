package com.alexiliescu.javajokes.exceptions;

public class NoJokesAvailableException extends RuntimeException {
    public NoJokesAvailableException(String message) {
        super(message);
    }
}