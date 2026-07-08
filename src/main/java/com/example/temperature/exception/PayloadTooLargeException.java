package com.example.temperature.exception;

public class PayloadTooLargeException extends RuntimeException {

    public PayloadTooLargeException(String message) {
        super(message);
    }
}
