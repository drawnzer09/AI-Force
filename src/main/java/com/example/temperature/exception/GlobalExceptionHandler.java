package com.example.temperature.exception;

import com.example.temperature.dto.ErrorDetailResponse;
import com.example.temperature.dto.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<ErrorResponse> handleApiException(ApiException ex) {
        List<ErrorDetailResponse> details = ex.getField()
                .map(field -> List.of(new ErrorDetailResponse(field, ex.getMessage())))
                .orElseGet(List::of);
        return build(ex.getErrorCode(), ex.getMessage(), details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<ErrorDetailResponse> details = ex.getConstraintViolations().stream()
                .map(violation -> new ErrorDetailResponse(lastPathSegment(violation.getPropertyPath().toString()), violation.getMessage()))
                .toList();
        return build(ErrorCode.INVALID_REQUEST, "Request validation failed", details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "Invalid value for parameter " + ex.getName();
        return build(ErrorCode.INVALID_REQUEST, message, List.of(new ErrorDetailResponse(ex.getName(), message)));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponse> handleDataAccess(DataAccessException ex) {
        log.error("Database operation failed", ex);
        return build(ErrorCode.INTERNAL_ERROR, "Unexpected service error", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected service error", ex);
        return build(ErrorCode.INTERNAL_ERROR, "Unexpected service error", List.of());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ErrorDetailResponse> details = new ArrayList<>();
        boolean payloadTooLarge = false;
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.add(new ErrorDetailResponse(fieldError.getField(), fieldError.getDefaultMessage()));
            if ("dataPoints".equals(fieldError.getField()) && "Size".equals(fieldError.getCode())) {
                payloadTooLarge = true;
            }
        }
        ErrorCode code = payloadTooLarge ? ErrorCode.PAYLOAD_TOO_LARGE : ErrorCode.INVALID_REQUEST;
        String message = payloadTooLarge ? "Batch exceeds allowed size" : "Request validation failed";
        return buildObject(code, message, details);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return buildObject(ErrorCode.INVALID_REQUEST, "Malformed JSON request", List.of());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return buildObject(
                ErrorCode.INVALID_REQUEST,
                "Missing required query parameter",
                List.of(new ErrorDetailResponse(ex.getParameterName(), ex.getParameterName() + " is required"))
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return buildObject(ErrorCode.UNSUPPORTED_MEDIA_TYPE, "Unsupported media type", List.of());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return buildObject(ErrorCode.METHOD_NOT_ALLOWED, "Method not allowed", List.of());
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return buildObject(ErrorCode.NOT_FOUND, "Resource not found", List.of());
    }

    private ResponseEntity<ErrorResponse> build(ErrorCode errorCode, String message, List<ErrorDetailResponse> details) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode.getCode(), message, details));
    }

    private ResponseEntity<Object> buildObject(ErrorCode errorCode, String message, List<ErrorDetailResponse> details) {
        return ResponseEntity.status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode.getCode(), message, details));
    }

    private String lastPathSegment(String path) {
        int index = path.lastIndexOf('.');
        return index >= 0 ? path.substring(index + 1) : path;
    }
}
