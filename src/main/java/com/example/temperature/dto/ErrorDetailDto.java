package com.example.temperature.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorDetailDto(
        String field,
        String issue
) {
}
