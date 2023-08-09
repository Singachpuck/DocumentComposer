package com.kpi.composer.exception;

public class FormatMismatchException extends RuntimeException {

    public FormatMismatchException(String message) {
        super(message);
    }

    public FormatMismatchException(String message, Throwable cause) {
        super(message, cause);
    }
}
