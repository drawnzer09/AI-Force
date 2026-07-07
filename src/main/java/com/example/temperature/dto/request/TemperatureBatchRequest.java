package com.example.temperature.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public class TemperatureBatchRequest {

    @NotNull(message = "records is required")
    @Size(min = 1, max = 1000, message = "records must contain between 1 and 1000 items")
    private List<@Valid TemperatureRecordRequest> records;

    public List<TemperatureRecordRequest> getRecords() {
        return records;
    }

    public void setRecords(List<TemperatureRecordRequest> records) {
        this.records = records;
    }
}
