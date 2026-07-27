package com.example.temperature.api.error;

import java.util.List;

public record ErrorBody(
        String code,
        String message,
        List<ErrorDetailResponse> details
) {
}
