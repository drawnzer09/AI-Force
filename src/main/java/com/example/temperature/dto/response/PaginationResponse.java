package com.example.temperature.dto.response;

public record PaginationResponse(
        int limit,
        int offset,
        int returnedCount
) {
}
