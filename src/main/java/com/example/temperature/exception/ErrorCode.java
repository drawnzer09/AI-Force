package com.example.temperature.exception;

import org.springframework.http.HttpStatus;

public enum ErrorCode {

    BAD_REQUEST("BAD_REQUEST", HttpStatus.BAD_REQUEST, "The request is invalid."),
    NOT_FOUND("NOT_FOUND", HttpStatus.NOT_FOUND, "The requested resource was not found."),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", HttpStatus.METHOD_NOT_ALLOWED, "The HTTP method is not supported."),
    PAYLOAD_TOO_LARGE("PAYLOAD_TOO_LARGE", HttpStatus.PAYLOAD_TOO_LARGE, "The request payload is too large."),
    UNSUPPORTED_MEDIA_TYPE(
            "UNSUPPORTED_MEDIA_TYPE",
            HttpStatus.UNSUPPORTED_MEDIA_TYPE,
            "The request content type is not supported."
    ),
    VALIDATION_ERROR("VALIDATION_ERROR", HttpStatus.UNPROCESSABLE_ENTITY, "The request failed validation."),
    INTERNAL_SERVER_ERROR(
            "INTERNAL_SERVER_ERROR",
            HttpStatus.INTERNAL_SERVER_ERROR,
            "An unexpected service error occurred."
    ),
    SERVICE_UNAVAILABLE(
            "SERVICE_UNAVAILABLE",
            HttpStatus.SERVICE_UNAVAILABLE,
            "The service is currently unavailable."
    );

    private final String code;
    private final HttpStatus status;
    private final String defaultMessage;

    ErrorCode(String code, HttpStatus status, String defaultMessage) {
        this.code = code;
        this.status = status;
        this.defaultMessage = defaultMessage;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getDefaultMessage() {
        return defaultMessage;
    }
}
