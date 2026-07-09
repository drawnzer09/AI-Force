package com.example.temperature.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public class IngestTemperatureReadingsRequest {

    @NotEmpty(message = "readings must contain at least one item")
    @Size(max = 1000, message = "readings must contain at most 1000 items")
    @Valid
    private List<TemperatureReadingRequest> readings;

    public List<TemperatureReadingRequest> getReadings() {
        return readings;
    }

    public void setReadings(List<TemperatureReadingRequest> readings) {
        this.readings = readings;
    }
}
