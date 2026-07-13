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
@Table(name = "temperature_records")
public class TemperatureRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "temperature_record_id", nullable = false, updatable = false)
    private Long id;

    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    @Column(name = "temperature", nullable = false)
    private BigDecimal temperature;

    protected TemperatureRecordEntity() {
    }

    public TemperatureRecordEntity(OffsetDateTime recordedAt, BigDecimal temperature) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof TemperatureRecordEntity that)) {
            return false;
        }
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return TemperatureRecordEntity.class.hashCode();
    }
}
