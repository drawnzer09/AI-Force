package com.example.temperature.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class IngestTemperatureReadingsRequest {

    @NotEmpty(message = "readings must contain at least one item")
    @Valid
    private List<TemperatureReadingRequest> readings;

    public List<TemperatureReadingRequest> getReadings() {
        return readings;
    }

    public void setReadings(List<TemperatureReadingRequest> readings) {
        this.readings = readings;
    }
}
