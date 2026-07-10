package com.example.temperature.dto.request;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TemperatureRecordRequest {

    @NotNull(message = "timestamp is required")
    private OffsetDateTime timestamp;

    @NotNull(message = "temperature is required")
    private BigDecimal temperature;

    public TemperatureRecordRequest() {
    }

    public TemperatureRecordRequest(OffsetDateTime timestamp, BigDecimal temperature) {
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }
}
