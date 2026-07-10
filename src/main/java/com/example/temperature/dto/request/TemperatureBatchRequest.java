package com.example.temperature.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class TemperatureBatchRequest {

    @NotEmpty(message = "records is required and must contain at least one item")
    @Size(max = 1000, message = "records must contain no more than 1000 items")
    @Valid
    private List<TemperatureRecordRequest> records;

    public TemperatureBatchRequest() {
    }

    public TemperatureBatchRequest(List<TemperatureRecordRequest> records) {
        this.records = records;
    }

    public List<TemperatureRecordRequest> getRecords() {
        return records;
    }

    public void setRecords(List<TemperatureRecordRequest> records) {
        this.records = records;
    }
}
