package com.example.temperature.config;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.temperature")
public record ApplicationProperties(
        @Min(1)
        @Max(1000)
        int maxBatchSize,

        @Min(1)
        @Max(1000)
        int defaultPageSize,

        @Min(1)
        @Max(1000)
        int maxPageSize
) {
}
