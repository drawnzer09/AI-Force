package com.example.temperature.exception;

import java.util.Optional;

public class ApiException extends RuntimeException {

    private final ErrorCode errorCode;
    private final String field;

    public ApiException(ErrorCode errorCode, String message) {
        this(errorCode, message, null);
    }

    public ApiException(ErrorCode errorCode, String message, String field) {
        super(message);
        this.errorCode = errorCode;
        this.field = field;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public Optional<String> getField() {
        return Optional.ofNullable(field);
    }
}
