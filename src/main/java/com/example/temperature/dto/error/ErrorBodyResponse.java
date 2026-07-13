package com.example.temperature.dto.error;

import java.util.List;

public record ErrorBodyResponse(
        String code,
        String message,
        List<ErrorDetailResponse> details
) {
}
