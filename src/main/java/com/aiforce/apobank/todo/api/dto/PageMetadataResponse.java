package com.aiforce.apobank.todo.api.dto;

public record PageMetadataResponse(
        int number,
        int size,
        long totalItems,
        int totalPages
) {
}
