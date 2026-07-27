package com.example.temperature.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "temperature_data_points")
public class TemperatureDataPointEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temperature_data_point_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @Column(name = "temperature", nullable = false, precision = 38, scale = 10)
    private BigDecimal temperature;

    protected TemperatureDataPointEntity() {
    }

    public TemperatureDataPointEntity(OffsetDateTime recordedAt, BigDecimal temperature) {
        this.recordedAt = recordedAt;
        this.temperature = temperature;
    }

    public Long getId() {
        return id;
    }

    public OffsetDateTime getRecordedAt() {
        return recordedAt;
    }

    public void setRecordedAt(OffsetDateTime recordedAt) {
        this.recordedAt = recordedAt;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }
}
