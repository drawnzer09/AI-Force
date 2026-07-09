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
@Table(name = "temperature_readings")
public class TemperatureReadingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "source_id", nullable = false)
    private String sourceId;

    @Column(name = "reading_timestamp", nullable = false)
    private OffsetDateTime readingTimestamp;

    @Column(name = "temperature", nullable = false)
    private BigDecimal temperature;

    @Column(name = "unit", nullable = false, length = 1)
    private String unit;

    @Column(name = "ingested_at", insertable = false, updatable = false)
    private OffsetDateTime ingestedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public OffsetDateTime getReadingTimestamp() {
        return readingTimestamp;
    }

    public void setReadingTimestamp(OffsetDateTime readingTimestamp) {
        this.readingTimestamp = readingTimestamp;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public void setTemperature(BigDecimal temperature) {
        this.temperature = temperature;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public OffsetDateTime getIngestedAt() {
        return ingestedAt;
    }

    public void setIngestedAt(OffsetDateTime ingestedAt) {
        this.ingestedAt = ingestedAt;
    }
}
