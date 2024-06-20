package org.example.shortlinkgenerator.controllers;

import jakarta.validation.ConstraintViolationException;
import org.example.shortlinkgenerator.exceptions.RegistrationException;
import org.example.shortlinkgenerator.exceptions.ShortUrlCreateException;
import org.example.shortlinkgenerator.exceptions.ShortUrlNotFoundException;
import org.example.shortlinkgenerator.exceptions.UserNotFoundException;
import org.example.shortlinkgenerator.utill.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler({ShortUrlCreateException.class, ConstraintViolationException.class, RegistrationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<ResponseError> handleBadRequestException(RuntimeException e) {
        return new ResponseEntity<>(
                createResponseError(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ShortUrlNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseError> handleNotFoundException(RuntimeException e) {
        return new ResponseEntity<>(
                createResponseError(e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    private ResponseError createResponseError(String errorMessage) {
        return new ResponseError(
                errorMessage,
                System.currentTimeMillis()
        );
    }
}

