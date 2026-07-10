package com.example.temperature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "temperature_records")
public class TemperatureRecordEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "\"timestamp\"", nullable = false, columnDefinition = "timestamp with time zone")
    private OffsetDateTime timestamp;

    @Column(name = "temperature", nullable = false, precision = 38, scale = 10)
    private BigDecimal temperature;

    public TemperatureRecordEntity() {
    }

    public TemperatureRecordEntity(OffsetDateTime timestamp, BigDecimal temperature) {
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
