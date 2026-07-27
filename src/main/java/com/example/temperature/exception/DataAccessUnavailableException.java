package com.example.temperature.exception;

public class DataAccessUnavailableException extends RuntimeException {

    public DataAccessUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
