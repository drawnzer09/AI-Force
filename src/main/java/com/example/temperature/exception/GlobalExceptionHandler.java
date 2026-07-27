package com.example.temperature.exception;

import com.example.temperature.api.error.ErrorBody;
import com.example.temperature.api.error.ErrorDetailResponse;
import com.example.temperature.api.error.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ErrorDetailResponse> details = new ArrayList<>();
        details.addAll(ex.getBindingResult().getFieldErrors().stream()
                .map(this::toFieldErrorDetail)
                .toList());
        details.addAll(ex.getBindingResult().getGlobalErrors().stream()
                .map(error -> new ErrorDetailResponse(error.getObjectName(), resolveMessage(error)))
                .toList());

        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {
        List<ErrorDetailResponse> details = ex.getConstraintViolations().stream()
                .map(violation -> new ErrorDetailResponse(violation.getPropertyPath().toString(), violation.getMessage()))
                .toList();
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", "Request validation failed", details);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex) {
        return build(
                HttpStatus.BAD_REQUEST,
                ex.getCode(),
                ex.getMessage(),
                List.of(new ErrorDetailResponse(ex.getField(), ex.getDetailMessage()))
        );
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return build(
                HttpStatus.BAD_REQUEST,
                "BAD_REQUEST",
                "The request is malformed or contains invalid parameter values",
                List.of(new ErrorDetailResponse(null, ex.getMessage()))
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED_MEDIA_TYPE",
                "Unsupported media type",
                List.of(new ErrorDetailResponse("Content-Type", ex.getMessage()))
        );
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponse> handleNotFound(Exception ex) {
        return build(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "The requested path does not match a defined endpoint",
                List.of(new ErrorDetailResponse(null, ex.getMessage()))
        );
    }

    @ExceptionHandler({DataAccessUnavailableException.class, DataAccessResourceFailureException.class})
    public ResponseEntity<ErrorResponse> handleDataAccessUnavailable(Exception ex) {
        return build(
                HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE",
                "The service is temporarily unable to access temperature data",
                List.of(new ErrorDetailResponse(null, ex.getMessage()))
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex) {
        LOGGER.error("Unexpected server error", ex);
        return build(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "An unexpected server-side failure occurred",
                List.of()
        );
    }

    private ErrorDetailResponse toFieldErrorDetail(FieldError fieldError) {
        return new ErrorDetailResponse(fieldError.getField(), resolveMessage(fieldError));
    }

    private String resolveMessage(DefaultMessageSourceResolvable resolvable) {
        return resolvable.getDefaultMessage() == null ? "Invalid value" : resolvable.getDefaultMessage();
    }

    private ResponseEntity<ErrorResponse> build(
            HttpStatus status,
            String code,
            String message,
            List<ErrorDetailResponse> details
    ) {
        return ResponseEntity.status(status)
                .body(new ErrorResponse(new ErrorBody(code, message, details)));
    }
}
