package com.example.temperature.api.dto;

public record ErrorDetail(
        String field,
        String message
) {
}
