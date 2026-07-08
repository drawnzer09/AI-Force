package com.example.temperature.exception;

import com.example.temperature.dto.ErrorDetailDto;
import com.example.temperature.dto.ErrorResponseDto;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.dao.DataAccessException;
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

import java.util.Comparator;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        List<ErrorDetailDto> details = ex.getBindingResult().getFieldErrors().stream()
                .sorted(Comparator.comparing(FieldError::getField))
                .map(fieldError -> new ErrorDetailDto(fieldError.getField(), resolveFieldErrorIssue(fieldError)))
                .toList();

        boolean payloadTooLarge = ex.getBindingResult().getFieldErrors().stream()
                .anyMatch(this::isRecordsTooLargeError);

        if (payloadTooLarge) {
            return build(HttpStatus.PAYLOAD_TOO_LARGE,
                    "PAYLOAD_TOO_LARGE",
                    "Submitted batch exceeds accepted size",
                    details);
        }

        return build(HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                "Request validation failed",
                details);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ErrorResponseDto> handleConstraintViolation(ConstraintViolationException ex) {
        List<ErrorDetailDto> details = ex.getConstraintViolations().stream()
                .sorted(Comparator.comparing(violation -> violation.getPropertyPath().toString()))
                .map(this::toDetail)
                .toList();

        return build(HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                "Request validation failed",
                details);
    }

    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            ConversionFailedException.class,
            MissingServletRequestParameterException.class,
            InvalidTimestampRangeException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBadRequest(Exception ex) {
        return build(HttpStatus.BAD_REQUEST,
                "INVALID_REQUEST",
                "Invalid request syntax, parameters, or body",
                List.of(new ErrorDetailDto(resolveField(ex), resolveIssue(ex))));
    }

    @ExceptionHandler(PayloadTooLargeException.class)
    public ResponseEntity<ErrorResponseDto> handlePayloadTooLarge(PayloadTooLargeException ex) {
        return build(HttpStatus.PAYLOAD_TOO_LARGE,
                "PAYLOAD_TOO_LARGE",
                "Submitted batch exceeds accepted size",
                List.of(new ErrorDetailDto("records", ex.getMessage())));
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErrorResponseDto> handleUnsupportedMediaType(HttpMediaTypeNotSupportedException ex) {
        return build(HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                "UNSUPPORTED_MEDIA_TYPE",
                "Request content type is unsupported",
                List.of(new ErrorDetailDto("Content-Type", ex.getMessage())));
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ErrorResponseDto> handleNotFound(Exception ex) {
        return build(HttpStatus.NOT_FOUND,
                "NOT_FOUND",
                "Requested endpoint does not exist",
                List.of());
    }

    @ExceptionHandler(PersistenceUnavailableException.class)
    public ResponseEntity<ErrorResponseDto> handlePersistenceUnavailable(PersistenceUnavailableException ex) {
        log.warn("Persistence unavailable", ex);
        return build(HttpStatus.SERVICE_UNAVAILABLE,
                "SERVICE_UNAVAILABLE",
                "Service is temporarily unable to serve requests",
                List.of(new ErrorDetailDto(null, ex.getMessage())));
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorResponseDto> handleDataAccess(DataAccessException ex) {
        log.error("Persistence failure", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Unexpected service failure",
                List.of());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleUnexpected(Exception ex) {
        log.error("Unexpected request failure", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "INTERNAL_SERVER_ERROR",
                "Unexpected service failure",
                List.of());
    }

    private ErrorDetailDto toDetail(ConstraintViolation<?> violation) {
        return new ErrorDetailDto(leafPropertyName(violation.getPropertyPath().toString()), violation.getMessage());
    }

    private String resolveFieldErrorIssue(FieldError fieldError) {
        return fieldError.getDefaultMessage() == null ? "invalid value" : fieldError.getDefaultMessage();
    }

    private boolean isRecordsTooLargeError(FieldError fieldError) {
        Object rejectedValue = fieldError.getRejectedValue();
        return "records".equals(fieldError.getField())
                && "Size".equals(fieldError.getCode())
                && rejectedValue instanceof List<?> list
                && list.size() > 1000;
    }

    private String resolveField(Exception ex) {
        if (ex instanceof MethodArgumentTypeMismatchException mismatchException) {
            return mismatchException.getName();
        }
        if (ex instanceof MissingServletRequestParameterException missingParameterException) {
            return missingParameterException.getParameterName();
        }
        if (ex instanceof InvalidTimestampRangeException) {
            return "fromTimestamp";
        }
        return null;
    }

    private String resolveIssue(Exception ex) {
        if (ex instanceof InvalidTimestampRangeException) {
            return ex.getMessage();
        }
        if (ex instanceof MethodArgumentTypeMismatchException mismatchException) {
            return "invalid value for parameter " + mismatchException.getName();
        }
        if (ex instanceof HttpMessageNotReadableException) {
            return "malformed JSON or invalid field value";
        }
        return ex.getMessage() == null ? "invalid request" : ex.getMessage();
    }

    private String leafPropertyName(String propertyPath) {
        int lastDot = propertyPath.lastIndexOf('.');
        return lastDot >= 0 ? propertyPath.substring(lastDot + 1) : propertyPath;
    }

    private ResponseEntity<ErrorResponseDto> build(HttpStatus status,
                                                   String code,
                                                   String message,
                                                   List<ErrorDetailDto> details) {
        return ResponseEntity.status(status).body(ErrorResponseDto.of(code, message, details));
    }
}
