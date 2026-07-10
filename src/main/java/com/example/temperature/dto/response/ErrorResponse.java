package com.example.temperature.dto.response;

import java.util.List;

public class ErrorResponse {

    private String code;
    private String message;
    private List<ErrorDetailResponse> details;
    private String requestId;

    public ErrorResponse() {
    }

    public ErrorResponse(String code, String message, List<ErrorDetailResponse> details, String requestId) {
        this.code = code;
        this.message = message;
        this.details = details;
        this.requestId = requestId;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<ErrorDetailResponse> getDetails() {
        return details;
    }

    public void setDetails(List<ErrorDetailResponse> details) {
        this.details = details;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
