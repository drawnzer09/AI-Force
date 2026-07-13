package com.example.temperature.dto.response;

public record PageMetadataResponse(
        int page,
        int size,
        int returnedCount
) {
}
