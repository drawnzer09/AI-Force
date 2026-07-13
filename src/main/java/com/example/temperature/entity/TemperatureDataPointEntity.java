package com.example.temperature.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

@Entity
@Table(name = "temperature_data_points")
public class TemperatureDataPointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temperature_data_point_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "\"timestamp\"", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime timestamp;

    @Column(name = "temperature", nullable = false, precision = 38, scale = 10)
    private BigDecimal temperature;

    public TemperatureDataPointEntity() {
    }

    public TemperatureDataPointEntity(OffsetDateTime timestamp, BigDecimal temperature) {
        this.timestamp = timestamp;
        this.temperature = temperature;
    }

    public Long getId() {
        return id;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemperatureDataPointEntity that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
