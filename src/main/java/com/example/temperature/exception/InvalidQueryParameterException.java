package com.example.temperature.exception;

import com.example.temperature.dto.error.ErrorDetail;

import java.util.List;

public class InvalidQueryParameterException extends RuntimeException {

    private final List<ErrorDetail> details;

    public InvalidQueryParameterException(String message, List<ErrorDetail> details) {
        super(message);
        this.details = details;
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }
}
