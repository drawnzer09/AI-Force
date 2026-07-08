package com.example.temperature.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public class TemperatureReadingKey implements Serializable {

    private OffsetDateTime readingTimestamp;
    private BigDecimal temperatureValue;

    public TemperatureReadingKey() {
    }

    public TemperatureReadingKey(OffsetDateTime readingTimestamp, BigDecimal temperatureValue) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemperatureReadingKey that)) {
            return false;
        }
        return Objects.equals(readingTimestamp, that.readingTimestamp)
                && Objects.equals(temperatureValue, that.temperatureValue);
    }

    @Override
    public int hashCode() {
        return Objects.hash(readingTimestamp, temperatureValue);
    }
}
