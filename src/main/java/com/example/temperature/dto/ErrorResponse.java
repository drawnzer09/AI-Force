package com.example.temperature.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public record ErrorResponse(ErrorBody error) {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public record ErrorBody(String code, String message, List<ErrorDetailResponse> details) {
    }

    public static ErrorResponse of(String code, String message) {
        return new ErrorResponse(new ErrorBody(code, message, List.of()));
    }

    public static ErrorResponse of(String code, String message, List<ErrorDetailResponse> details) {
        return new ErrorResponse(new ErrorBody(code, message, details == null ? List.of() : details));
    }
}
