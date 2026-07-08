package com.example.temperature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "temperature_readings")
@IdClass(TemperatureReadingKey.class)
public class TemperatureReadingEntity {

    @Id
    @Column(name = "reading_timestamp", nullable = false)
    private OffsetDateTime readingTimestamp;

    @Id
    @Column(name = "temperature_value", nullable = false)
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
