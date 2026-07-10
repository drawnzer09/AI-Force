package com.example.temperature.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TemperatureRecordResponse {

    private OffsetDateTime timestamp;
    private BigDecimal temperature;

    public TemperatureRecordResponse() {
    }

    public TemperatureRecordResponse(OffsetDateTime timestamp, BigDecimal temperature) {
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
