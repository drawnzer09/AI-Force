package com.example.temperature.dto.error;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiErrorDetail(
        String field,
        String issue
) {
}
