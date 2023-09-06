package com.assessment.casinoapi.exception;

import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;

import java.io.IOException;

@ControllerAdvice
public class CustomErrorHandler {
    @ExceptionHandler(ValidationException.class)
    public void handleValidationException(final ValidationException exception,
                                                   final ServletWebRequest webRequest) throws IOException {
        if (webRequest.getResponse() != null) {
            webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public void handleMethodArgumentNotValidException(final MethodArgumentNotValidException exception,
                                                   final ServletWebRequest webRequest) throws IOException {
        if (webRequest.getResponse() != null) {
            webRequest.getResponse().sendError(HttpStatus.BAD_REQUEST.value(), exception.getMessage());
        }
    }
}
