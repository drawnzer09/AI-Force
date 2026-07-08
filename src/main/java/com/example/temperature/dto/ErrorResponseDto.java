package com.example.temperature.dto;

import java.util.List;

public record ErrorResponseDto(
        ErrorDto error
) {

    public record ErrorDto(
            String code,
            String message,
            List<ErrorDetailDto> details
    ) {
    }

    public static ErrorResponseDto of(String code, String message, List<ErrorDetailDto> details) {
        return new ErrorResponseDto(new ErrorDto(code, message, details == null ? List.of() : details));
    }
}
