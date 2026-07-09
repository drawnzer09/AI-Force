package com.example.temperature.dto.response;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TemperatureReadingResponse {

    private String sourceId;
    private OffsetDateTime timestamp;
    private BigDecimal temperature;
    private String unit;

    public TemperatureReadingResponse(String sourceId, OffsetDateTime timestamp, BigDecimal temperature, String unit) {
        this.sourceId = sourceId;
        this.timestamp = timestamp;
        this.temperature = temperature;
        this.unit = unit;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
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

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
