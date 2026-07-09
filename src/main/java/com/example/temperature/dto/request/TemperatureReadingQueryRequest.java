package com.example.temperature.dto.request;

import java.time.OffsetDateTime;

public class TemperatureReadingQueryRequest {

    private OffsetDateTime from;
    private OffsetDateTime to;
    private String sourceId;
    private String unit;
    private Integer limit;
    private Integer offset;
    private String sort;

    public TemperatureReadingQueryRequest(OffsetDateTime from, OffsetDateTime to, String sourceId, String unit, Integer limit, Integer offset, String sort) {
        this.from = from;
        this.to = to;
        this.sourceId = sourceId;
        this.unit = unit;
        this.limit = limit;
        this.offset = offset;
        this.sort = sort;
    }

    public OffsetDateTime getFrom() {
        return from;
    }

    public void setFrom(OffsetDateTime from) {
        this.from = from;
    }

    public OffsetDateTime getTo() {
        return to;
    }

    public void setTo(OffsetDateTime to) {
        this.to = to;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }
}
