package com.example.temperature.exception;

import java.util.List;

public class BadRequestException extends RuntimeException {

    private final List<FieldError> fieldErrors;

    public BadRequestException(String message, List<FieldError> fieldErrors) {
        super(message);
        this.fieldErrors = List.copyOf(fieldErrors);
    }

    public List<FieldError> getFieldErrors() {
        return fieldErrors;
    }

    public record FieldError(String field, String message) {
    }
}
