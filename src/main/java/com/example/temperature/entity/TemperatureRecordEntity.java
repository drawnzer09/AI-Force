package com.example.temperature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "temperature_records")
public class TemperatureRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "measurement_timestamp", nullable = false)
    private OffsetDateTime measurementTimestamp;

    @Column(name = "temperature", nullable = false)
    private BigDecimal temperature;

    @Column(name = "ingested_at", nullable = false, insertable = false, updatable = false)
    private OffsetDateTime ingestedAt;

    protected TemperatureRecordEntity() {
    }

    public TemperatureRecordEntity(OffsetDateTime measurementTimestamp, BigDecimal temperature) {
        this.measurementTimestamp = measurementTimestamp;
        this.temperature = temperature;
    }

    public Long getId() {
        return id;
    }

    public OffsetDateTime getMeasurementTimestamp() {
        return measurementTimestamp;
    }

    public void setMeasurementTimestamp(OffsetDateTime measurementTimestamp) {
        this.measurementTimestamp = measurementTimestamp;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public OffsetDateTime getIngestedAt() {
        return ingestedAt;
    }
}
