package com.example.temperature.api.error;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetailResponse(
        String field,
        String message
) {
}
