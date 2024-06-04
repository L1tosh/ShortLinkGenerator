package org.example.shortlinkgenerator.exceptions;

public class ShortUrlCreateException extends RuntimeException {
    public static final String DEFAULT_MSG = "Problem creating a new short link";
    public ShortUrlCreateException() {
        super(DEFAULT_MSG);
    }
    public ShortUrlCreateException(String msg) {
        super(msg);
    }
}
