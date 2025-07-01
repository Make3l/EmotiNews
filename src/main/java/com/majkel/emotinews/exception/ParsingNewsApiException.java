package com.majkel.emotinews.exception;

public class ParsingNewsApiException extends RuntimeException {
    public ParsingNewsApiException(String message) {
        super(message);
    }
    public ParsingNewsApiException(String message, Throwable cause) {
        super(message,cause);
    }
}
