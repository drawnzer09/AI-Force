package com.example.temperature.exception;

import com.example.temperature.dto.error.ApiErrorDetail;
import com.example.temperature.dto.error.ApiErrorResponse;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ApiErrorDetail> details = ex.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .map(error -> new ApiErrorDetail(error.getField(), resolveMessage(error)))
                .toList();

        return build(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR", "Request validation failed", details);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        if (ex.getCause() instanceof InvalidFormatException invalidFormatException && isBodyValidationFormatError(invalidFormatException)) {
            String field = jsonPath(invalidFormatException.getPath());
            List<ApiErrorDetail> details = List.of(new ApiErrorDetail(field, "value has an invalid format"));
            return build(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_ERROR", "Request validation failed", details);
        }

        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Request JSON is malformed", List.of());
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "Content type must be application/json", List.of());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ApiErrorDetail> details = List.of(new ApiErrorDetail(ex.getParameterName(), "required query parameter is missing"));
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Request query parameters are invalid", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<ApiErrorDetail> details = ex.getConstraintViolations().stream()
                .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
                .map(this::toDetail)
                .toList();
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Request query parameters are invalid", details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        List<ApiErrorDetail> details = List.of(new ApiErrorDetail(ex.getName(), "value has an invalid format"));
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Request query parameters are invalid", details);
    }

    @ExceptionHandler(InvalidQueryException.class)
    public ResponseEntity<Object> handleInvalidQuery(InvalidQueryException ex) {
        List<ApiErrorDetail> details = List.of(new ApiErrorDetail(ex.getField(), ex.getIssue()));
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Request query parameters are invalid", details);
    }

    @ExceptionHandler(PayloadTooLargeException.class)
    public ResponseEntity<Object> handlePayloadTooLarge(PayloadTooLargeException ex) {
        List<ApiErrorDetail> details = List.of(new ApiErrorDetail(ex.getField(), ex.getIssue()));
        return build(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "Request payload is too large", details);
    }

    @ExceptionHandler(DataAccessResourceFailureException.class)
    public ResponseEntity<Object> handleDataAccessResourceFailure(DataAccessResourceFailureException ex) {
        log.error("Database resource failure", ex);
        return build(HttpStatus.SERVICE_UNAVAILABLE, "SERVICE_UNAVAILABLE", "Service is temporarily unavailable", List.of());
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Object> handleDataAccess(DataAccessException ex) {
        log.error("Persistence failure", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected server error occurred", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception ex) {
        log.error("Unexpected request handling failure", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected server error occurred", List.of());
    }

    private ResponseEntity<Object> build(HttpStatus status, String code, String message, List<ApiErrorDetail> details) {
        return ResponseEntity.status(status).body(ApiErrorResponse.of(code, message, details));
    }

    private ApiErrorDetail toDetail(ConstraintViolation<?> violation) {
        return new ApiErrorDetail(lastPathNode(violation.getPropertyPath().toString()), violation.getMessage());
    }

    private String resolveMessage(FieldError error) {
        String defaultMessage = error.getDefaultMessage();
        return defaultMessage == null ? "value is invalid" : defaultMessage;
    }

    private boolean isBodyValidationFormatError(InvalidFormatException ex) {
        Class<?> targetType = ex.getTargetType();
        return OffsetDateTime.class.equals(targetType) || BigDecimal.class.equals(targetType);
    }

    private String jsonPath(List<JsonMappingException.Reference> references) {
        if (references == null || references.isEmpty()) {
            return null;
        }

        StringBuilder builder = new StringBuilder();
        for (JsonMappingException.Reference reference : references) {
            if (reference.getFieldName() != null) {
                if (!builder.isEmpty()) {
                    builder.append('.');
                }
                builder.append(reference.getFieldName());
            } else if (reference.getIndex() >= 0) {
                builder.append('[').append(reference.getIndex()).append(']');
            }
        }
        return builder.isEmpty() ? null : builder.toString();
    }

    private String lastPathNode(String path) {
        if (path == null || path.isBlank()) {
            return path;
        }
        int index = path.lastIndexOf('.');
        return index >= 0 ? path.substring(index + 1) : path;
    }
}
