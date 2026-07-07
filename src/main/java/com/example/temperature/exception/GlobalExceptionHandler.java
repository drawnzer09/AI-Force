package com.example.temperature.exception;

import com.example.temperature.dto.error.ErrorBody;
import com.example.temperature.dto.error.ErrorDetail;
import com.example.temperature.dto.error.ErrorEnvelope;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.async.AsyncRequestNotUsableException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorEnvelope> handleBadRequest(BadRequestException ex) {
        List<ErrorDetail> details = ex.getFieldErrors().stream()
                .map(error -> new ErrorDetail(error.field(), error.message()))
                .toList();
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), details);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorEnvelope> handleValidation(MethodArgumentNotValidException ex) {
        List<ErrorDetail> details = new ArrayList<>();
        for (FieldError fieldError : ex.getBindingResult().getFieldErrors()) {
            details.add(new ErrorDetail(fieldError.getField(), fieldError.getDefaultMessage()));
        }
        return error(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", details);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            TypeMismatchException.class,
            MissingServletRequestParameterException.class,
            ConstraintViolationException.class
    })
    public ResponseEntity<ErrorEnvelope> handleMalformedRequest(Exception ex) {
        return error(HttpStatus.BAD_REQUEST, "BAD_REQUEST", "Malformed or invalid request", List.of(new ErrorDetail(null, rootMessage(ex))));
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ErrorEnvelope> handleNotFound(NoHandlerFoundException ex) {
        return error(HttpStatus.NOT_FOUND, "NOT_FOUND", "The requested path does not exist", List.of(new ErrorDetail("path", ex.getRequestURL())));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorEnvelope> handleMethodNotAllowed(HttpRequestMethodNotSupportedException ex) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, "METHOD_NOT_ALLOWED", "HTTP method is not supported for this path", List.of(new ErrorDetail("method", ex.getMethod())));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorEnvelope> handlePayloadTooLarge(MaxUploadSizeExceededException ex) {
        return error(HttpStatus.PAYLOAD_TOO_LARGE, "PAYLOAD_TOO_LARGE", "Request payload is too large", List.of());
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorEnvelope> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "UNSUPPORTED_MEDIA_TYPE", "Request content type is not supported", List.of(new ErrorDetail("Content-Type", String.valueOf(ex.getContentType()))));
    }

    @ExceptionHandler({PersistenceUnavailableException.class, DataAccessException.class})
    public ResponseEntity<ErrorEnvelope> handlePersistence(Exception ex) {
        LOGGER.error("Persistence error", ex);
        return error(HttpStatus.SERVICE_UNAVAILABLE, "PERSISTENCE_UNAVAILABLE", "Persistence connectivity is unavailable", List.of());
    }

    @ExceptionHandler(AsyncRequestNotUsableException.class)
    public void handleDisconnectedClient(AsyncRequestNotUsableException ex) {
        LOGGER.debug("Client disconnected before response could be written", ex);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEnvelope> handleUnexpected(Exception ex, HttpServletRequest request) {
        LOGGER.error("Unexpected error while handling {} {}", request.getMethod(), request.getRequestURI(), ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR", "An unexpected server error occurred", List.of());
    }

    private ResponseEntity<ErrorEnvelope> error(HttpStatus status, String code, String message, List<ErrorDetail> details) {
        return ResponseEntity.status(status).body(new ErrorEnvelope(new ErrorBody(code, message, details)));
    }

    private String rootMessage(Exception ex) {
        Throwable current = ex;
        while (current.getCause() != null) {
            current = current.getCause();
        }
        return current.getMessage() == null ? ex.getMessage() : current.getMessage();
    }
}
