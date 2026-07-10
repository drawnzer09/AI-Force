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
    @Column(name = "temperature_record_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "recorded_at", nullable = false, columnDefinition = "timestamptz")
    private OffsetDateTime recordedAt;

    @Column(name = "temperature_value", nullable = false)
    private BigDecimal temperatureValue;

    protected TemperatureRecordEntity() {
    }

    public TemperatureRecordEntity(OffsetDateTime recordedAt, BigDecimal temperatureValue) {
        this.recordedAt = recordedAt;
        this.temperatureValue = temperatureValue;
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

    public BigDecimal getTemperatureValue() {
        return temperatureValue;
    }

    public void setTemperatureValue(BigDecimal temperatureValue) {
        this.temperatureValue = temperatureValue;
    }
}
