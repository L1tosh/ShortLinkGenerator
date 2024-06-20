package org.example.shortlinkgenerator.controllers;

import jakarta.validation.ConstraintViolationException;
import org.example.shortlinkgenerator.exceptions.RegistrationException;
import org.example.shortlinkgenerator.exceptions.ShortUrlCreateException;
import org.example.shortlinkgenerator.exceptions.ShortUrlNotFoundException;
import org.example.shortlinkgenerator.exceptions.UserNotFoundException;
import org.example.shortlinkgenerator.utill.ResponseError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(ShortUrlCreateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    private ResponseEntity<ResponseError> handleException(ShortUrlCreateException e) {
        return new ResponseEntity<>(
                createResponseError(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ShortUrlNotFoundException.class, UserNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ResponseError> handleException(RuntimeException e) {
        return new ResponseEntity<>(
                createResponseError(e.getMessage()),
                HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseError> handleException(ConstraintViolationException e) {
        return new ResponseEntity<>(
                createResponseError(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RegistrationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<ResponseError> handleValidationExceptions(RegistrationException e) {
        return new ResponseEntity<>(
                createResponseError(e.getMessage()),
                HttpStatus.BAD_REQUEST);
    }

//    @ExceptionHandler(UserNotFoundException.class)
//    @ResponseStatus(HttpStatus.NOT_FOUND)
//    public ResponseEntity<ResponseError> handleException(UserNotFoundException e) {
//        return new ResponseEntity<>(
//                createResponseError(e.getMessage()),
//                HttpStatus.NOT_FOUND);
//    }

    private ResponseError createResponseError(String errorMessage) {
        return new ResponseError(
                errorMessage,
                System.currentTimeMillis()
        );
    }
}

