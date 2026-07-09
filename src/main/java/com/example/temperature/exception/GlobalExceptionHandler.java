package com.example.temperature.exception;

import com.example.temperature.dto.error.ErrorBody;
import com.example.temperature.dto.error.ErrorDetail;
import com.example.temperature.dto.error.ErrorEnvelope;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorEnvelope> handleInvalidRequest(InvalidRequestException exception) {
        HttpStatus status = exception.isPayloadTooLarge() ? HttpStatus.PAYLOAD_TOO_LARGE : HttpStatus.UNPROCESSABLE_ENTITY;
        ValidationErrorCode code = exception.isPayloadTooLarge()
                ? ValidationErrorCode.PAYLOAD_TOO_LARGE
                : ValidationErrorCode.VALIDATION_FAILED;
        List<ErrorDetail> details = exception.getFieldIssues().stream()
                .map(issue -> new ErrorDetail(issue.field(), issue.issue()))
                .toList();
        return build(status, code, exception.getMessage(), details);
    }

    @ExceptionHandler(InvalidQueryParameterException.class)
    public ResponseEntity<ErrorEnvelope> handleInvalidQueryParameter(InvalidQueryParameterException exception) {
        List<ErrorDetail> details = exception.getFieldIssues().stream()
                .map(issue -> new ErrorDetail(issue.field(), issue.issue()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, ValidationErrorCode.BAD_REQUEST, exception.getMessage(), details);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorEnvelope> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException exception) {
        return build(
                HttpStatus.BAD_REQUEST,
                ValidationErrorCode.BAD_REQUEST,
                "Invalid request parameter",
                List.of(new ErrorDetail(exception.getName(), "parameter has an invalid value"))
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorEnvelope> handleConstraintViolation(ConstraintViolationException exception) {
        List<ErrorDetail> details = exception.getConstraintViolations().stream()
                .map(violation -> new ErrorDetail(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        return build(HttpStatus.UNPROCESSABLE_ENTITY, ValidationErrorCode.VALIDATION_FAILED, "Request validation failed", details);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorEnvelope> handleMaxUploadSizeExceeded(MaxUploadSizeExceededException exception) {
        return build(
                HttpStatus.PAYLOAD_TOO_LARGE,
                ValidationErrorCode.PAYLOAD_TOO_LARGE,
                "Request payload is too large",
                List.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorEnvelope> handleDataIntegrityViolation(DataIntegrityViolationException exception) {
        log.warn("Database integrity validation failed", exception);
        return build(
                HttpStatus.UNPROCESSABLE_ENTITY,
                ValidationErrorCode.VALIDATION_FAILED,
                "Request validation failed",
                List.of(new ErrorDetail("request", "submitted data violates persistence constraints"))
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEnvelope> handleUnexpected(Exception exception) {
        log.error("Unexpected service error", exception);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ValidationErrorCode.INTERNAL_SERVER_ERROR,
                "An unexpected service error occurred",
                List.of()
        );
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        List<ErrorDetail> details = exception.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String field = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
                    String issue = error.getDefaultMessage() == null ? "invalid value" : error.getDefaultMessage();
                    return new ErrorDetail(field, issue);
                })
                .toList();
        return asObject(build(HttpStatus.UNPROCESSABLE_ENTITY, ValidationErrorCode.VALIDATION_FAILED, "Request validation failed", details));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return asObject(build(
                HttpStatus.BAD_REQUEST,
                ValidationErrorCode.BAD_REQUEST,
                "Request JSON is malformed or contains invalid values",
                List.of(new ErrorDetail("body", "request body could not be parsed"))
        ));
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return asObject(build(
                HttpStatus.BAD_REQUEST,
                ValidationErrorCode.BAD_REQUEST,
                "Missing required query parameter",
                List.of(new ErrorDetail(exception.getParameterName(), "parameter is required"))
        ));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return asObject(build(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                ValidationErrorCode.UNSUPPORTED_MEDIA_TYPE,
                "Unsupported media type",
                List.of(new ErrorDetail("Content-Type", "supported media type is application/json"))
        ));
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return asObject(build(
                HttpStatus.METHOD_NOT_ALLOWED,
                ValidationErrorCode.METHOD_NOT_ALLOWED,
                "HTTP method is not supported for this resource",
                List.of(new ErrorDetail("method", "unsupported method " + exception.getMethod()))
        ));
    }

    private ResponseEntity<ErrorEnvelope> build(HttpStatus status, ValidationErrorCode code, String message, List<ErrorDetail> details) {
        ErrorBody body = new ErrorBody(code.name(), message, details == null ? List.of() : details);
        return ResponseEntity.status(status).body(new ErrorEnvelope(body));
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private ResponseEntity<Object> asObject(ResponseEntity<ErrorEnvelope> response) {
        return (ResponseEntity) response;
    }
}
