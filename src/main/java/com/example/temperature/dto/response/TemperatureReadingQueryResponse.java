package com.example.temperature.dto.response;

import java.util.List;

public class TemperatureReadingQueryResponse {

    private List<TemperatureReadingResponse> items;
    private PaginationResponse pagination;

    public TemperatureReadingQueryResponse(List<TemperatureReadingResponse> items, PaginationResponse pagination) {
        this.items = items;
        this.pagination = pagination;
    }

    public List<TemperatureReadingResponse> getItems() {
        return items;
    }

    public void setItems(List<TemperatureReadingResponse> items) {
        this.items = items;
    }

    public PaginationResponse getPagination() {
        return pagination;
    }

    public void setPagination(PaginationResponse pagination) {
        this.pagination = pagination;
    }
}
