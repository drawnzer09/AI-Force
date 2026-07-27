package com.example.temperature.exception;

public class InvalidRequestException extends RuntimeException {

    private final String code;
    private final String field;
    private final String detailMessage;

    public InvalidRequestException(String code, String message, String field, String detailMessage) {
        super(message);
        this.code = code;
        this.field = field;
        this.detailMessage = detailMessage;
    }

    public String getCode() {
        return code;
    }

    public String getField() {
        return field;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
