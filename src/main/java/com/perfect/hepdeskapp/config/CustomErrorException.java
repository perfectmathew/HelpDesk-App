package com.perfect.hepdeskapp.config;

public class CustomErrorException extends RuntimeException {
    public CustomErrorException(String errorMessage) {
        super(errorMessage);
    }

    public CustomErrorException(String errorMessage, Throwable throwable){
        super(errorMessage,throwable);
    }
}
