package com.example.temperature.exception;

public class InvalidQueryRangeException extends RuntimeException {

    public InvalidQueryRangeException(String message) {
        super(message);
    }
}
