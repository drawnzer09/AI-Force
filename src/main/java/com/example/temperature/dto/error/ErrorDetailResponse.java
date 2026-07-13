package com.example.temperature.dto.error;

public record ErrorDetailResponse(
        String field,
        String message
) {
}
