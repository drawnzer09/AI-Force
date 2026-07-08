package com.example.temperature.exception;

public class PersistenceUnavailableException extends RuntimeException {

    public PersistenceUnavailableException(String message) {
        super(message);
    }

    public PersistenceUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
