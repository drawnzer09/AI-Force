package com.example.temperature.dto.response;

public class ErrorEnvelopeResponse {

    private ErrorResponse error;

    public ErrorEnvelopeResponse() {
    }

    public ErrorEnvelopeResponse(ErrorResponse error) {
        this.error = error;
    }

    public ErrorResponse getError() {
        return error;
    }

    public void setError(ErrorResponse error) {
        this.error = error;
    }
}
