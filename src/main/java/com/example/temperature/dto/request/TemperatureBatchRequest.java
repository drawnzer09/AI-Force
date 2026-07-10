package com.example.temperature.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.List;

public record TemperatureBatchRequest(
        @NotNull(message = "records is required")
        @Size(min = 1, message = "records must contain at least 1 item")
        List<@Valid TemperatureRecordRequest> records
) {
}
