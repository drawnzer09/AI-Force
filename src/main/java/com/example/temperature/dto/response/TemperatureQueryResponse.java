package com.example.temperature.dto.response;

import java.util.List;

public class TemperatureQueryResponse {

    private List<TemperatureRecordResponse> records;
    private PaginationResponse pagination;

    public TemperatureQueryResponse() {
    }

    public TemperatureQueryResponse(List<TemperatureRecordResponse> records, PaginationResponse pagination) {
        this.records = records;
        this.pagination = pagination;
    }

    public List<TemperatureRecordResponse> getRecords() {
        return records;
    }

    public void setRecords(List<TemperatureRecordResponse> records) {
        this.records = records;
    }

    public PaginationResponse getPagination() {
        return pagination;
    }

    public void setPagination(PaginationResponse pagination) {
        this.pagination = pagination;
    }
}
