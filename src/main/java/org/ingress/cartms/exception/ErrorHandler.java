package org.ingress.cartms.exception;

import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.DefaultErrorAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.ingress.cartms.exception.ExceptionConstraints.UNEXPECTED_EXCEPTION_CODE;
import static org.ingress.cartms.exception.ExceptionConstraints.UNEXPECTED_EXCEPTION_MESSAGE;

@RestControllerAdvice
public class ErrorHandler extends DefaultErrorAttributes {

    private static final Logger log = LoggerFactory.getLogger(ErrorHandler.class);

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ExceptionResponse handle(Exception ex) {
        log.error("Exception: ", ex);
        return new ExceptionResponse(UNEXPECTED_EXCEPTION_CODE, UNEXPECTED_EXCEPTION_MESSAGE);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ExceptionResponse handle(NotFoundException ex) {
        log.error("NotFoundException: ", ex);
        return new ExceptionResponse(ex.getCode(), ex.getMessage());
    }

    @ExceptionHandler(CustomFeignException.class)
    public ResponseEntity<ExceptionResponse> handle(CustomFeignException ex) {
        log.error("CustomFeignException: {}", ex.getMessage());
        return ResponseEntity.status(ex.getStatus()).body(
                ExceptionResponse.builder()
                        .message(ex.getMessage())
                        .build()
        );
    }
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationExceptions(MethodArgumentNotValidException ex, WebRequest request) {
        List<ConstraintsViolationError> errors = ex.getBindingResult().getFieldErrors().stream()
                .map(fieldError -> new ConstraintsViolationError(
                        fieldError.getField(),
                        //fieldError.getRejectedValue(),
                        fieldError.getDefaultMessage()
                ))
                .collect(Collectors.toList());

        return buildErrorResponse(request, HttpStatus.BAD_REQUEST, "Validation error", errors);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> handleJsonParseException(HttpMessageNotReadableException ex, WebRequest request) {
        List<ConstraintsViolationError> errors = List.of(new ConstraintsViolationError(
                "JSON Parse Error",
                "Invalid input format or missing required fields"
        ));

        return buildErrorResponse(request, HttpStatus.BAD_REQUEST, "Validation error", errors);
    }

    private ResponseEntity<Map<String, Object>> buildErrorResponse(WebRequest request, HttpStatus status, String message, List<ConstraintsViolationError> errors) {
        Map<String, Object> attributes = getErrorAttributes(request, ErrorAttributeOptions.defaults());
        attributes.put("status", status.value());
        attributes.put("error", message);
        attributes.put("path", ((ServletWebRequest) request).getRequest().getRequestURI());
        attributes.put("fieldErrors", errors);
        return new ResponseEntity<>(attributes, status);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ExceptionResponse> handle(AuthenticationException ex) {
        log.error("AuthenticationException: ", ex);
        return ResponseEntity.status(ex.getStatus()).body(
                ExceptionResponse.builder()
                        .code(ex.getCode())
                        .message(ex.getMessage())
                        .build()
        );
    }
}