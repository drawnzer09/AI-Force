package com.example.temperature.entity;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class TemperatureReadingEntity {

    private OffsetDateTime readingTimestamp;
    private BigDecimal temperatureValue;

    public TemperatureReadingEntity() {
    }

    public TemperatureReadingEntity(OffsetDateTime readingTimestamp, BigDecimal temperatureValue) {
        this.readingTimestamp = readingTimestamp;
        this.temperatureValue = temperatureValue;
    }

    public OffsetDateTime getReadingTimestamp() {
        return readingTimestamp;
    }

    public void setReadingTimestamp(OffsetDateTime readingTimestamp) {
        this.readingTimestamp = readingTimestamp;
    }

    public BigDecimal getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(BigDecimal temperatureValue) {
        this.temperatureValue = temperatureValue;
    }
}
