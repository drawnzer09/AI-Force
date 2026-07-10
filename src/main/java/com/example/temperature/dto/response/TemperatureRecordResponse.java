package com.example.temperature.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TemperatureRecordResponse {

    private UUID id;
    private OffsetDateTime timestamp;
    private BigDecimal temperature;

    public TemperatureRecordResponse() {
    }

    public TemperatureRecordResponse(UUID id, OffsetDateTime timestamp, BigDecimal temperature) {
        this.id = id;
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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
