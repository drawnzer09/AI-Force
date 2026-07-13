package com.example.temperature.exception;

import com.example.temperature.dto.error.ErrorBodyResponse;
import com.example.temperature.dto.error.ErrorDetailResponse;
import com.example.temperature.dto.error.ErrorEnvelopeResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request
    ) {
        List<ErrorDetailResponse> details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .map(error -> new ErrorDetailResponse(error.getField(), error.getDefaultMessage()))
                .toList();

        log.warn("Request validation failed: {}", details);
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_FAILED", "Validation failed", details);
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(
            HandlerMethodValidationException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request
    ) {
        List<ErrorDetailResponse> details = new ArrayList<>();
        ex.getAllValidationResults().forEach(result -> {
            String parameterName = result.getMethodParameter().getParameterName();
            for (MessageSourceResolvable error : result.getResolvableErrors()) {
                details.add(new ErrorDetailResponse(parameterName, error.getDefaultMessage()));
            }
        });

        log.warn("Handler method validation failed: {}", details);
        return buildResponse(HttpStatus.UNPROCESSABLE_ENTITY, "VALIDATION_FAILED", "Validation failed", details);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request
    ) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "MALFORMED_JSON",
                "Malformed JSON request",
                List.of(new ErrorDetailResponse(null, "Request body is malformed or contains invalid field values"))
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request
    ) {
        return buildResponse(
                HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED_MEDIA_TYPE",
                "Unsupported media type",
                List.of(new ErrorDetailResponse("Content-Type", "Content-Type must be application/json"))
        );
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request
    ) {
        return buildResponse(
                HttpStatus.METHOD_NOT_ALLOWED,
                "METHOD_NOT_ALLOWED",
                "Method not allowed",
                List.of(new ErrorDetailResponse(null, ex.getMessage()))
        );
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(
            NoHandlerFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "Resource not found",
                List.of(new ErrorDetailResponse("path", ex.getRequestURL()))
        );
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(
            NoResourceFoundException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            org.springframework.web.context.request.WebRequest request
    ) {
        return buildResponse(
                HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "Resource not found",
                List.of(new ErrorDetailResponse("path", ex.getResourcePath()))
        );
    }

    @ExceptionHandler(InvalidQueryParameterException.class)
    public ResponseEntity<Object> handleInvalidQueryParameter(InvalidQueryParameterException ex) {
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                ex.getCode(),
                ex.getMessage(),
                List.of(new ErrorDetailResponse(ex.getField(), ex.getDetailMessage()))
        );
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Object> handleMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String field = ex.getName();
        String message = "Invalid value for parameter " + field;
        return buildResponse(
                HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST_PARAMETER",
                "Invalid request parameter",
                List.of(new ErrorDetailResponse(field, message))
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolation(ConstraintViolationException ex) {
        List<ErrorDetailResponse> details = ex.getConstraintViolations()
                .stream()
                .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
                .map(this::toErrorDetail)