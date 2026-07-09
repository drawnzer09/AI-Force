package com.example.temperature.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TemperatureReadingRequest {

    @NotBlank(message = "sourceId is required")
    @Size(min = 1, max = 128, message = "sourceId must be between 1 and 128 characters")
    private String sourceId;

    @NotNull(message = "timestamp is required")
    private OffsetDateTime timestamp;

    @NotNull(message = "temperature is required")
    private BigDecimal temperature;

    @NotBlank(message = "unit is required")
    @Pattern(regexp = "C|F|K", message = "unit must be one of C, F, K")
    private String unit;

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
