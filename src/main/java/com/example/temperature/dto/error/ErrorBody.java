package com.example.temperature.dto.error;

import java.util.List;

public record ErrorBody(
        String code,
        String message,
        List<ErrorDetail> details
) {
}
