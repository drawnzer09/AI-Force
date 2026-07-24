package com.aiforce.apobank.todo.api.error;

import java.time.Instant;
import java.util.List;

public record ApiError(
        String code,
        String message,
        List<ApiErrorDetail> details,
        Instant timestamp
) {
}
