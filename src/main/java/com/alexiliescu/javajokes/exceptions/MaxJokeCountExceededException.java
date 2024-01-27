package com.alexiliescu.javajokes.exceptions;

public class MaxJokeCountExceededException extends RuntimeException {
    public MaxJokeCountExceededException(String message) {
        super(message);
    }
}