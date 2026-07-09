package com.example.temperature.dto.error;

public class ErrorEnvelope {

    private ErrorBody error;

    public ErrorEnvelope(ErrorBody error) {
        this.error = error;
    }

    public ErrorBody getError() {
        return error;
    }

    public void setError(ErrorBody error) {
        this.error = error;
    }
}
