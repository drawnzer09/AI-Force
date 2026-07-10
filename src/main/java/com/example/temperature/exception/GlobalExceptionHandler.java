package com.example.temperature.exception;

import com.example.temperature.dto.error.ErrorBody;
import com.example.temperature.dto.error.ErrorDetail;
import com.example.temperature.dto.error.ErrorEnvelope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    private static final String REQUEST_ID_HEADER = "X-Request-Id";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorEnvelope> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest request) {
        List<ErrorDetail> details = new ArrayList<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                details.add(new ErrorDetail(error.getField(), error.getDefaultMessage()))
        );

        ex.getBindingResult().getGlobalErrors().forEach(error ->
                details.add(new ErrorDetail(error.getObjectName(), error.getDefaultMessage()))
        );

        LOGGER.warn("Request validation failed: {}", details);
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Request validation failed", details, request);
    }

    @ExceptionHandler(InvalidQueryParameterException.class)
    public ResponseEntity<ErrorEnvelope> handleInvalidQueryParameter(
            InvalidQueryParameterException ex,
            HttpServletRequest request
    ) {
        List<ErrorDetail> details = List.of(new ErrorDetail(ex.getField(), ex.getDetailMessage()));
        LOGGER.warn("Invalid query parameter {}: {}", ex.getField(), ex.getDetailMessage());
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Invalid query parameter", details, request);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class,
            ConversionFailedException.class
    })
    public ResponseEntity<ErrorEnvelope> handleBadRequest(Exception ex, HttpServletRequest request) {
        LOGGER.warn("Bad request", ex);
        List<ErrorDetail> details = List.of(new ErrorDetail(null, ex.getMessage()));
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Bad request", details, request);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorEnvelope> handleNoHandlerFound(NoHandlerFoundException ex, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(new ErrorDetail("path", ex.getRequestURL()));
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", "Requested path was not found", details, request);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorEnvelope> handleNoResourceFound(NoResourceFoundException ex, HttpServletRequest request) {
        List<ErrorDetail> details = List.of(new ErrorDetail("path", ex.getResourcePath()));
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", "Requested path was not found", details, request);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorEnvelope> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            HttpServletRequest request
    ) {
        List<ErrorDetail> details = List.of(new ErrorDetail("method", ex.getMethod()));
        return build(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "Method not allowed", details, request);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorEnvelope> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException ex,
            HttpServletRequest request
    ) {
        List<ErrorDetail> details = List.of(new ErrorDetail("contentType", String.valueOf(ex.getContentType())));
        return build(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED_MEDIA_TYPE",
                "Unsupported media type",
                details,
                request
        );
    }

    @ExceptionHandler(PersistenceUnavailableException.class)
    public ResponseEntity<ErrorEnvelope> handlePersistenceUnavailable(
            PersistenceUnavailableException ex,
            HttpServletRequest request
    ) {
        LOGGER.error("Persistence unavailable", ex);
        return build(
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE",
                "Persistence or required runtime dependency is unavailable",
                List.of(),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEnvelope> handleUnexpected(Exception ex, HttpServletRequest request) {
        LOGGER.error("Unexpected service error", ex);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected service error occurred",
                List.of(),
                request
        );
    }

    private ResponseEntity<ErrorEnvelope> build(
            HttpStatus status,
            String code,
            String message,
            List<ErrorDetail> details,
            HttpServletRequest request
    ) {
        String requestId = request.getHeader(REQUEST_ID_HEADER);
        if (requestId == null || requestId.isBlank()) {
            requestId = UUID.randomUUID().toString();
        }

        ErrorBody body = new ErrorBody(code, message, details, requestId);
        return ResponseEntity.status(status)
                .header(REQUEST_ID_HEADER, requestId)
                .body(new ErrorEnvelope(body));
    }
}
