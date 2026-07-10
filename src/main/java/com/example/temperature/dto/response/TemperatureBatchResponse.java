package com.example.temperature.dto.response;

import java.util.List;

public class TemperatureBatchResponse {

    private int acceptedCount;
    private List<TemperatureRecordResponse> records;

    public TemperatureBatchResponse() {
    }

    public TemperatureBatchResponse(int acceptedCount, List<TemperatureRecordResponse> records) {
        this.acceptedCount = acceptedCount;
        this.records = records;
    }

    public int getAcceptedCount() {
        return acceptedCount;
    }

    public void setAcceptedCount(int acceptedCount) {
        this.acceptedCount = acceptedCount;
    }

    public List<TemperatureRecordResponse> getRecords() {
        return records;
    }

    public void setRecords(List<TemperatureRecordResponse> records) {
        this.records = records;
    }
}
