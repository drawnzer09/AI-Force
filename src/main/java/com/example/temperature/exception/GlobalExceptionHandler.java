package com.example.temperature.exception;

import com.example.temperature.config.RequestIdFilter;
import com.example.temperature.dto.response.ErrorDetailResponse;
import com.example.temperature.dto.response.ErrorEnvelopeResponse;
import com.example.temperature.dto.response.ErrorResponse;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleMethodArgumentNotValid(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        List<ErrorDetailResponse> details = exception.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::toDetail)
                .toList();

        return buildResponse(ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.getDefaultMessage(), details, request);
    }

    @ExceptionHandler(QueryValidationException.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleQueryValidation(
            QueryValidationException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                ErrorCode.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.getDefaultMessage(),
                List.of(new ErrorDetailResponse(exception.getField(), exception.getIssue())),
                request
        );
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleConstraintViolation(
            ConstraintViolationException exception,
            HttpServletRequest request
    ) {
        List<ErrorDetailResponse> details = exception.getConstraintViolations()
                .stream()
                .map(this::toDetail)
                .toList();

        return buildResponse(ErrorCode.VALIDATION_ERROR, ErrorCode.VALIDATION_ERROR.getDefaultMessage(), details, request);
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MissingServletRequestParameterException.class,
            ServletRequestBindingException.class
    })
    public ResponseEntity<ErrorEnvelopeResponse> handleBadRequest(Exception exception, HttpServletRequest request) {
        return buildResponse(
                ErrorCode.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.getDefaultMessage(),
                List.of(new ErrorDetailResponse(resolveField(exception), exception.getMessage())),
                request
        );
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleMessageNotReadable(
            HttpMessageNotReadableException exception,
            HttpServletRequest request
    ) {
        List<ErrorDetailResponse> details = new ArrayList<>();
        Throwable mostSpecificCause = exception.getMostSpecificCause();

        if (mostSpecificCause instanceof InvalidFormatException invalidFormatException) {
            String field = invalidFormatException.getPath()
                    .stream()
                    .map(reference -> reference.getFieldName() == null
                            ? "[" + reference.getIndex() + "]"
                            : reference.getFieldName())
                    .filter(Objects::nonNull)
                    .collect(Collectors.joining("."));
            details.add(new ErrorDetailResponse(field, "value has an invalid format"));
        } else {
            details.add(new ErrorDetailResponse("body", "request JSON is malformed or invalid"));
        }

        return buildResponse(ErrorCode.BAD_REQUEST, ErrorCode.BAD_REQUEST.getDefaultMessage(), details, request);
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleMessageConversion(
            HttpMessageConversionException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                ErrorCode.BAD_REQUEST,
                ErrorCode.BAD_REQUEST.getDefaultMessage(),
                List.of(new ErrorDetailResponse("body", "request JSON could not be converted")),
                request
        );
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleUnsupportedMediaType(
            HttpMediaTypeNotSupportedException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                ErrorCode.UNSUPPORTED_MEDIA_TYPE,
                ErrorCode.UNSUPPORTED_MEDIA_TYPE.getDefaultMessage(),
                List.of(new ErrorDetailResponse("Content-Type", exception.getMessage())),
                request
        );
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleMethodNotSupported(
            HttpRequestMethodNotSupportedException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                ErrorCode.METHOD_NOT_ALLOWED,
                ErrorCode.METHOD_NOT_ALLOWED.getDefaultMessage(),
                List.of(new ErrorDetailResponse("method", exception.getMessage())),
                request
        );
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorEnvelopeResponse> handleNotFound(Exception exception, HttpServletRequest request) {
        return buildResponse(
                ErrorCode.NOT_FOUND,
                ErrorCode.NOT_FOUND.getDefaultMessage(),
                List.of(new ErrorDetailResponse("path", request.getRequestURI())),
                request
        );
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleMaxUploadSizeExceeded(
            MaxUploadSizeExceededException exception,
            HttpServletRequest request
    ) {
        return buildResponse(
                ErrorCode.PAYLOAD_TOO_LARGE,
                ErrorCode.PAYLOAD_TOO_LARGE.getDefaultMessage(),
                List.of(new ErrorDetailResponse("body", "request payload exceeds the maximum supported size")),
                request
        );
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleDataAccess(DataAccessException exception, HttpServletRequest request) {
        LOGGER.error("Persistence dependency failure", exception);
        return buildResponse(
                ErrorCode.SERVICE_UNAVAILABLE,
                ErrorCode.SERVICE_UNAVAILABLE.getDefaultMessage(),
                List.of(new ErrorDetailResponse("database", "required persistence dependency is unavailable")),
                request
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorEnvelopeResponse> handleUnexpected(Exception exception, HttpServletRequest request) {
        LOGGER.error("Unexpected service error", exception);
        return buildResponse(
                ErrorCode.INTERNAL_SERVER_ERROR,
                ErrorCode.INTERNAL_SERVER_ERROR.getDefaultMessage(),
