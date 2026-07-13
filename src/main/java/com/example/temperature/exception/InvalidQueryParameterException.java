package com.example.temperature.exception;

import com.example.temperature.dto.error.ErrorDetail;

import java.util.List;

public class InvalidQueryParameterException extends RuntimeException {

    private final List<ErrorDetail> details;

    public InvalidQueryParameterException(List<ErrorDetail> details) {
        super("Invalid query parameter");
        this.details = List.copyOf(details);
    }

    public List<ErrorDetail> getDetails() {
        return details;
    }
}
