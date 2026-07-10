package com.example.temperature.dto.error;

import java.util.List;

public record ApiErrorResponse(ApiError error) {

    public static ApiErrorResponse of(String code, String message) {
        return new ApiErrorResponse(new ApiError(code, message, List.of()));
    }

    public static ApiErrorResponse of(String code, String message, List<ApiErrorDetail> details) {
        return new ApiErrorResponse(new ApiError(code, message, details == null ? List.of() : details));
    }

    public record ApiError(
            String code,
            String message,
            List<ApiErrorDetail> details
    ) {
    }
}
