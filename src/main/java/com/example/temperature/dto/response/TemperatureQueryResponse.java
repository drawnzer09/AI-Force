package com.example.temperature.dto.response;

import java.util.List;

public class TemperatureQueryResponse {

    private List<TemperatureRecordResponse> data;
    private PaginationResponse pagination;

    public TemperatureQueryResponse() {
    }

    public TemperatureQueryResponse(List<TemperatureRecordResponse> data, PaginationResponse pagination) {
        this.data = data;
        this.pagination = pagination;
    }

    public List<TemperatureRecordResponse> getData() {
        return data;
    }

    public void setData(List<TemperatureRecordResponse> data) {
        this.data = data;
    }

    public PaginationResponse getPagination() {
        return pagination;
    }

    public void setPagination(PaginationResponse pagination) {
        this.pagination = pagination;
    }
}
