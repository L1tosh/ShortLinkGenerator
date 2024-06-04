package org.example.shortlinkgenerator.exceptions;

public class ShortUrlNotFoundException extends RuntimeException {
    public static final String DEFAULT_MSG = "No entry with this link was found";
    public ShortUrlNotFoundException() {
        super(DEFAULT_MSG);
    }

    public ShortUrlNotFoundException(String msg) {
        super(msg);
    }
}
