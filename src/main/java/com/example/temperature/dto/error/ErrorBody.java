package com.example.temperature.dto.error;

import java.util.ArrayList;
import java.util.List;

public class ErrorBody {

    private String code;
    private String message;
    private List<ErrorDetail> details = new ArrayList<>();
    private String requestId;

    public ErrorBody() {
    }

    public ErrorBody(String code, String message, List<ErrorDetail> details, String requestId) {
        this.code = code;
        this.message = message;
        this.details = details == null ? new ArrayList<>() : details;
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

    public List<ErrorDetail> getDetails() {
        return details;
    }

    public void setDetails(List<ErrorDetail> details) {
        this.details = details == null ? new ArrayList<>() : details;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
}
