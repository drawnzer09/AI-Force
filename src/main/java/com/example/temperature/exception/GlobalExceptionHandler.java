package com.example.temperature.exception;

import com.example.temperature.api.dto.ErrorBody;
import com.example.temperature.api.dto.ErrorDetail;
import com.example.temperature.api.dto.ErrorResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .map(error -> new ErrorDetail(normalizeField(error.getField()), message(error)))
                .toList();

        boolean oversizedBatch = ex.getBindingResult().getFieldErrors().stream()
                .anyMatch(error -> "records".equals(error.getField())
                        && error.getRejectedValue() instanceof List<?> list
                        && list.size() > 1000);

        if (oversizedBatch) {
            log.warn("Payload too large: ingestion batch exceeded maximum size");
            return build(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "Request payload is too large.", details);
        }

        log.warn("Request body validation failed: {}", details);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request validation failed.", details);
    }

    @ExceptionHandler(HandlerMethodValidationException.class)
    public ResponseEntity<ErrorResponse> handleHandlerMethodValidation(HandlerMethodValidationException ex) {
        List<ErrorDetail> details = ex.getAllValidationResults().stream()
                .flatMap(result -> result.getResolvableErrors().stream()
                        .map(error -> new ErrorDetail(
                                Objects.requireNonNullElse(result.getMethodParameter().getParameterName(), "request"),
                                Objects.requireNonNullElse(error.getDefaultMessage(), "is invalid")
                        )))
                .toList();

        log.warn("Request parameter validation failed: {}", details);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request validation failed.", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<ErrorDetail> details = ex.getConstraintViolations().stream()
                .map(this::toDetail)
                .sorted(Comparator.comparing(ErrorDetail::field))
                .toList();

        log.warn("Constraint validation failed: {}", details);
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_FAILED", "Request validation failed.", details);
    }

    @ExceptionHandler(InvalidQueryRangeException.class)
    public ResponseEntity<ErrorResponse> handleInvalidQueryRange(InvalidQueryRangeException ex) {
        log.warn("Invalid query range: {}", ex.getMessage());
        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                "Request validation failed.",
                List.of(new ErrorDetail("startTimestamp", ex.getMessage()))
        );
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingServletRequestParameter(MissingServletRequestParameterException ex) {
        log.warn("Missing required query parameter: {}", ex.getParameterName());
        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                "Request validation failed.",
                List.of(new ErrorDetail(ex.getParameterName(), "is required"))
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String message = "must be a valid value";
        if (ex.getRequiredType() != null && ex.getRequiredType().getSimpleName().contains("OffsetDateTime")) {
            message = "must be a valid ISO 8601 date-time string";
        }

        log.warn("Query parameter type mismatch for {}: {}", ex.getName(), ex.getMessage());
        return build(
                HttpStatus.BAD_REQUEST,
                "VALIDATION_FAILED",
                "Request validation failed.",
                List.of(new ErrorDetail(ex.getName(), message))
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = mostSpecificCause(ex);

        if (cause instanceof UnrecognizedPropertyException unrecognized) {
            String field = jsonPath(unrecognized.getPath(), unrecognized.getPropertyName());
            log.warn("Unknown JSON property at {}", field);
            return build(
                    HttpStatus.BAD_REQUEST,
                    "VALIDATION_FAILED",
                    "Request validation failed.",
                    List.of(new ErrorDetail(field, "is not allowed"))
            );
        }

        if (cause instanceof InvalidFormatException invalidFormat) {
            String field = jsonPath(invalidFormat.getPath(), "request");
            String message = invalidFormat.getTargetType() != null
                    && invalidFormat.getTargetType().getSimpleName().contains("OffsetDateTime")
                    ? "must be a valid ISO 8601 date-time string"
                    : "must be a valid value";
            log.warn("Invalid JSON value at {}: {}", field, invalidFormat.getOriginalMessage());
            return build(
                    HttpStatus.BAD_REQUEST,
                    "VALIDATION_FAILED",
                    "Request validation failed.",
                    List.of(new ErrorDetail(field, message))
            );
        }

        log.warn("Malformed JSON request: {}", ex.getMessage());
        return build(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_JSON",
                "Request body is malformed or unreadable.",
                List.of()
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex) {
        log.warn("Unsupported media type: {}", ex.getContentType());
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "Content type must be application/json.", List.of());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException ex) {
        log.warn("Request payload too large: {}", ex.getMessage());
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "Request payload is too large.", List.of());
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
        log.warn("Requested path not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", "The requested resource was not found.", List.of());
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.warn("HTTP method not supported: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", "The requested resource was not found.", List.of());
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ErrorResponse> handleResponseStatusException(ResponseStatusException ex) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        if (status == HttpStatus.SERVICE_UNAVAILABLE) {
            log.warn("Service unavailable: {}", ex.getReason());
            return build(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", "Service is not ready.", List.of());
        }
        log.warn("Response status exception: {}", ex.getMessage());
        return build(status, status.name(), Objects.requireNonNullElse(ex.getReason(), "Request failed."), List.of());
    }

    @ExceptionHandler({DataAccessException.class, AsyncRequestTimeoutException.class})
    public ResponseEntity<ErrorResponse> handleServiceFailure(Exception ex) {
        log.error("Service failure", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        log.error("Unexpected failure", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected error occurred.", List.of());
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String code, String message, List<ErrorDetail> details) {
        return ResponseEntity.status(status).body(new ErrorResponse(new ErrorBody(code, message, details)));
    }

    private ErrorDetail toDetail(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath() == null ? "request" : violation.getPropertyPath().toString();
        int lastDot = path.lastIndexOf('.');
        String field = lastDot >= 0 ? path.substring(lastDot + 1) : path;
        return new ErrorDetail(field, violation.getMessage());
    }

    private String message(DefaultMessageSourceResolvableLike error) {
        return Objects.requireNonNullElse(error.getDefaultMessage(), "is invalid");
    }

    private String message(FieldError error) {
        return Objects.requireNonNullElse(error.getDefaultMessage(), "is invalid");
    }

    private String normalizeField(String field) {
        return field.replace("records.", "records");
    }

    private Throwable mostSpecificCause(Throwable throwable) {
        Throwable current = throwable;
        while (current.getCause() != null && current.getCause() != current) {
            current = current.getCause();
        }
        return current;
    }

    private String jsonPath(List<JsonMappingException.Reference> references, String fallback) {
        if (references == null || references.isEmpty()) {
            return fallback;
        }

        StringBuilder path = new StringBuilder();
        for (JsonMappingException.Reference reference : references) {
            if (reference.getFieldName() != null) {
                if (!path.isEmpty()) {
                    path.append('.');
                }
                path.append(reference.getFieldName());
            } else if (reference.getIndex() >= 0) {
                path.append('[').append(reference.getIndex()).append(']');
            }
        }
        return path.isEmpty() ? fallback : path.toString();
    }

    private interface DefaultMessageSourceResolvableLike {
        String getDefaultMessage();
    }
}
