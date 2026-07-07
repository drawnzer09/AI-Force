package com.example.temperature.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "temperature_measurements")
public class TemperatureMeasurementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temperature_measurement_id", nullable = false)
    private Long id;

    @Column(name = "measurement_timestamp", nullable = false)
    private Instant measurementTimestamp;

    @Column(name = "temperature", nullable = false)
    private BigDecimal temperature;

    protected TemperatureMeasurementEntity() {
    }

    public TemperatureMeasurementEntity(Instant measurementTimestamp, BigDecimal temperature) {
        this.measurementTimestamp = measurementTimestamp;
        this.temperature = temperature;
    }

    public Long getId() {
        return id;
    }

    public Instant getMeasurementTimestamp() {
        return measurementTimestamp;
    }

    public void setMeasurementTimestamp(Instant measurementTimestamp) {
        this.measurementTimestamp = measurementTimestamp;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }
}
