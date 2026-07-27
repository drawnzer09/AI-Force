package com.example.temperature.api.dto;

public record PageMetadataResponse(
        int page,
        int size,
        int returnedCount
) {
}
