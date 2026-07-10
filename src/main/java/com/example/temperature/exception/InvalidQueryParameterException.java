package com.example.temperature.exception;

public class InvalidQueryParameterException extends RuntimeException {

    private final String field;
    private final String detailMessage;

    public InvalidQueryParameterException(String field, String detailMessage) {
        super(detailMessage);
        this.field = field;
        this.detailMessage = detailMessage;
    }

    public String getField() {
        return field;
    }

    public String getDetailMessage() {
        return detailMessage;
    }
}
