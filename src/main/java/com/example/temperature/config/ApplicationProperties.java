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
        public Pagination {
            if (defaultPageSize <= 0) {
                defaultPageSize = 100;
            }
            if (maxPageSize <= 0) {
                maxPageSize = 1000;
            }
            if (defaultPageSize > maxPageSize) {
                defaultPageSize = maxPageSize;
            }
        }
    }
}
