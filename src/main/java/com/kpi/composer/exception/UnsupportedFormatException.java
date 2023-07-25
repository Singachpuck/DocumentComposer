package com.kpi.composer.exception;

public class UnsupportedFormatException extends RuntimeException {

    public UnsupportedFormatException() {
        super();
    }


    public UnsupportedFormatException(String message) {
        super(message);
    }


    public UnsupportedFormatException(String message, Throwable cause) {
        super(message, cause);
    }
}
