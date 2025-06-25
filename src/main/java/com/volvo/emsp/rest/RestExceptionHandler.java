package com.volvo.emsp.rest;

import com.volvo.emsp.execption.BadRequestException;
import com.volvo.emsp.execption.InvalidBusinessOperationException;
import com.volvo.emsp.execption.ResourceAlreadyExistsException;
import com.volvo.emsp.execption.ResourceNotFoundException;
import com.volvo.emsp.rest.model.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.Date;
import java.util.List;

@RestControllerAdvice
public class RestExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse> handleValidation(
            MethodArgumentNotValidException ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        String path = getRequestURI(request);
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .toList();
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed",
                        errors,
                        path,
                        new Date()
                )
        );
    }

    @ExceptionHandler({
            BadRequestException.class,
            HttpMessageNotReadableException.class,
            MethodArgumentTypeMismatchException.class,
            IllegalArgumentException.class,
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(
            Exception ex,
            WebRequest request
    ) {
        if (ex instanceof BadRequestException) {
            log.warn(ex.getMessage());
        } else {
            log.error(ex.getMessage(), ex);
        }
        String path = getRequestURI(request);
        return ResponseEntity.badRequest().body(
                new ErrorResponse(
                        HttpStatus.BAD_REQUEST.value(),
                        "Validation Failed",
                        List.of(ex.getMessage()),
                        path,
                        new Date()
                )
        );
    }

    @ExceptionHandler({
            HttpRequestMethodNotSupportedException.class
    })
    public ResponseEntity<ErrorResponse> handleMethodNotAllowed(
            HttpRequestMethodNotSupportedException ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        String path = getRequestURI(request);
        return ResponseEntity.status(HttpStatus.METHOD_NOT_ALLOWED.value()).body(
                new ErrorResponse(
                        HttpStatus.METHOD_NOT_ALLOWED.value(),
                        "Method Not Allowed",
                        List.of(ex.getMessage()),
                        path,
                        new Date()
                )
        );
    }

    @ExceptionHandler({
            HttpMediaTypeNotSupportedException.class
    })
    public ResponseEntity<ErrorResponse> handleMediaTypeNotSupported(
            HttpMediaTypeNotSupportedException ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        String path = getRequestURI(request);
        return ResponseEntity.status(HttpStatus.UNSUPPORTED_MEDIA_TYPE.value()).body(
                new ErrorResponse(
                        HttpStatus.UNSUPPORTED_MEDIA_TYPE.value(),
                        "Unsupported Media Type",
                        List.of(ex.getMessage()),
                        path,
                        new Date()
                )
        );
    }

    @ExceptionHandler({
            InvalidBusinessOperationException.class,
            UnsupportedOperationException.class,
            IllegalStateException.class}
    )
    public ResponseEntity<ErrorResponse> handleConflict(
            Exception ex,
            WebRequest request
    ) {
        if (ex instanceof InvalidBusinessOperationException) {
            log.warn(ex.getMessage());
        } else {
            log.error(ex.getMessage(), ex);
        }
        String path = getRequestURI(request);
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        "Unsupported Operation",
                        List.of(ex.getMessage()),
                        path,
                        new Date()
                )
        );
    }

    @ExceptionHandler({
            ResourceNotFoundException.class,
            NoResourceFoundException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<ErrorResponse> handleNotFound(
            Exception ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        String path = getRequestURI(request);
        return ResponseEntity.status(HttpStatus.NOT_FOUND.value()).body(
                new ErrorResponse(
                        HttpStatus.NOT_FOUND.value(),
                        "Resource not found",
                        List.of(ex.getMessage()),
                        path,
                        new Date()
                )
        );
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyExists(
            ResourceAlreadyExistsException ex,
            WebRequest request
    ) {
        log.warn(ex.getMessage());
        String path = getRequestURI(request);
        return ResponseEntity.status(HttpStatus.CONFLICT.value()).body(
                new ErrorResponse(
                        HttpStatus.CONFLICT.value(),
                        "Resource already exists",
                        List.of(ex.getMessage()),
                        path,
                        new Date()
                )
        );
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> all(
            Throwable ex,
            WebRequest request
    ) {
        log.error(ex.getMessage(), ex);
        String path = getRequestURI(request);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR.value()).body(
                new ErrorResponse(
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "Internal Server Error",
                        List.of(ex.getMessage()),
                        path,
                        new Date()
                )
        );
    }

    private static String getRequestURI(WebRequest request) {
        try {
            return ((ServletWebRequest) request).getRequest().getRequestURI();
        } catch (Exception e) {
            log.error("Could not get request URI", e);
            return "unknown";
        }
    }

}
