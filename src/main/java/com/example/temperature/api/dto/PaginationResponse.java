package com.example.temperature.api.dto;

public record PaginationResponse(
        int limit,
        long offset,
        int returned
) {
}
