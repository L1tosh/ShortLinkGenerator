package org.example.shortlinkgenerator.exceptions;

import org.springframework.validation.ObjectError;

import java.util.List;
import java.util.stream.Collectors;

public class RegistrationException extends RuntimeException{
    public RegistrationException(String msg) {
        super(msg);
    }

    public RegistrationException(List<ObjectError> exceptions) {
        super(exceptions.stream()
                .map(ObjectError::getDefaultMessage)
                .collect(Collectors.joining(", ")));
    }
}
