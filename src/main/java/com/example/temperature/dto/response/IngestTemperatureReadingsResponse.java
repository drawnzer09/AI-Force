package com.example.temperature.dto.response;

import java.util.List;

public class IngestTemperatureReadingsResponse {

    private int receivedCount;
    private int storedCount;
    private List<TemperatureReadingResponse> readings;

    public IngestTemperatureReadingsResponse(int receivedCount, int storedCount, List<TemperatureReadingResponse> readings) {
        this.receivedCount = receivedCount;
        this.storedCount = storedCount;
        this.readings = readings;
    }

    public int getReceivedCount() {
        return receivedCount;
    }

    public void setReceivedCount(int receivedCount) {
        this.receivedCount = receivedCount;
    }

    public int getStoredCount() {
        return storedCount;
    }

    public void setStoredCount(int storedCount) {
        this.storedCount = storedCount;
    }

    public List<TemperatureReadingResponse> getReadings() {
        return readings;
    }

    public void setReadings(List<TemperatureReadingResponse> readings) {
        this.readings = readings;
    }
}
