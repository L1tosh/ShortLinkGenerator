package org.example.shortlinkgenerator.controllers;

import jakarta.validation.ConstraintViolationException;
import org.example.shortlinkgenerator.exceptions.ShortUrlCreateException;
import org.example.shortlinkgenerator.exceptions.ShortUrlNotFoundException;
import org.example.shortlinkgenerator.utill.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ShortUrlCreateException.class)
    private ResponseEntity<ResponseError> handleException(ShortUrlCreateException e) {
        return new ResponseEntity<>(
                createResponseError(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ShortUrlNotFoundException.class)
    public ResponseEntity<ResponseError> handleException(ShortUrlNotFoundException e) {
        return new ResponseEntity<>(
                createResponseError(e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ResponseError> handleException(ConstraintViolationException e) {
        return new ResponseEntity<>(
                createResponseError(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    private ResponseError createResponseError(String errorMessage) {
        return new ResponseError(
                errorMessage,
                System.currentTimeMillis()
        );
    }
}

