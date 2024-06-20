package org.example.shortlinkgenerator.exceptions;

public class UserNotFoundException extends RuntimeException {
     public static final String DEFAULT_MSG = "User doesn't found";

     public UserNotFoundException() {
         super(DEFAULT_MSG);
     }
}
