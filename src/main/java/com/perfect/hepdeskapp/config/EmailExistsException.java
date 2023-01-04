package com.perfect.hepdeskapp.config;

public class EmailExistsException extends RuntimeException {
    public EmailExistsException(String errorMessage) {
        super(errorMessage);
    }

    public EmailExistsException(String errorMessage,  Throwable throwable){
        super(errorMessage,throwable);
    }
}
