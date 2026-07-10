package com.example.temperature.dto.response;

public class TemperatureBatchResponse {

    private int acceptedCount;

    public TemperatureBatchResponse() {
    }

    public TemperatureBatchResponse(int acceptedCount) {
        this.acceptedCount = acceptedCount;
    }

    public int getAcceptedCount() {
        return acceptedCount;
    }

    public void setAcceptedCount(int acceptedCount) {
        this.acceptedCount = acceptedCount;
    }
}
