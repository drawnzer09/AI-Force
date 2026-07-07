package com.example.temperature.dto.request;

import com.example.temperature.validation.FiniteTemperature;
import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public class TemperatureRecordRequest {

    @NotNull(message = "timestamp is required")
    private OffsetDateTime timestamp;

    @NotNull(message = "temperature is required")
    @FiniteTemperature
    private Double temperature;

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }
}
