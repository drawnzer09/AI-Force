package com.example.temperature.exception;

import com.example.temperature.dto.error.ErrorBody;
import com.example.temperature.dto.error.ErrorDetail;
import com.example.temperature.dto.error.ErrorEnvelope;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.core.convert.ConversionFailedException;
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
        List<ErrorDetail> details = ex.getBindingResult().getAllErrors().stream()
                .map(error -> {
                    String field = error instanceof FieldError fieldError ? fieldError.getField() : error.getObjectName();
                    String issue = error.getDefaultMessage() == null ? "invalid value" : error.getDefaultMessage();
                    return new ErrorDetail(field, issue);
                })
                .toList();
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ValidationErrorCode.VALIDATION_ERROR.name(), "Request validation failed", details);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(
            HttpMessageNotReadableException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return error(HttpStatus.BAD_REQUEST, ValidationErrorCode.BAD_REQUEST.name(), "Request JSON is malformed or contains invalid values", List.of());
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(
            MissingServletRequestParameterException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return error(HttpStatus.BAD_REQUEST, ValidationErrorCode.BAD_REQUEST.name(), "Required query parameter is missing", List.of(new ErrorDetail(ex.getParameterName(), "parameter is required")));
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return error(HttpStatus.UNSUPPORTED_MEDIA_TYPE, ValidationErrorCode.UNSUPPORTED_MEDIA_TYPE.name(), "Unsupported media type", List.of());
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(
            HttpRequestMethodNotSupportedException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        return error(HttpStatus.METHOD_NOT_ALLOWED, ValidationErrorCode.METHOD_NOT_ALLOWED.name(), "HTTP method is not supported for this path", List.of());
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<Object> handleInvalidRequest(InvalidRequestException ex) {
        List<ErrorDetail> details = ex.getIssues().stream()
                .map(issue -> new ErrorDetail(issue.field(), issue.issue()))
                .toList();
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ValidationErrorCode.VALIDATION_ERROR.name(), ex.getMessage(), details);
    }

    @ExceptionHandler(InvalidQueryParameterException.class)
    public ResponseEntity<Object> handleInvalidQueryParameter(InvalidQueryParameterException ex) {
        List<ErrorDetail> details = ex.getIssues().stream()
                .map(issue -> new ErrorDetail(issue.field(), issue.issue()))
                .toList();
        return error(HttpStatus.BAD_REQUEST, ValidationErrorCode.BAD_REQUEST.name(), ex.getMessage(), details);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConversionFailedException.class, ConstraintViolationException.class})
    public ResponseEntity<Object> handleBadRequest(Exception ex) {
        return error(HttpStatus.BAD_REQUEST, ValidationErrorCode.BAD_REQUEST.name(), "Request parameters are invalid", List.of());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.warn("Data integrity violation", ex);
        return error(HttpStatus.UNPROCESSABLE_ENTITY, ValidationErrorCode.VALIDATION_ERROR.name(), "Request violates persistence constraints", List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleUnexpected(Exception ex) {
        log.error("Unexpected service error", ex);
        return error(HttpStatus.INTERNAL_SERVER_ERROR, ValidationErrorCode.INTERNAL_ERROR.name(), "An unexpected service error occurred", List.of());
    }

    private ResponseEntity<Object> error(HttpStatus status, String code, String message, List<ErrorDetail> details) {
        return ResponseEntity.status(status).body(new ErrorEnvelope(new ErrorBody(code, message, details)));
    }
}
