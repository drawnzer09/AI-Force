package com.example.temperature.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "application")
public record ApplicationProperties(
        Pagination pagination
) {

    public ApplicationProperties {
        if (pagination == null) {
            pagination = new Pagination(100, 1000);
        }
    }

    public record Pagination(
            int defaultPageSize,
            int maxPageSize
    ) {
    }
}
