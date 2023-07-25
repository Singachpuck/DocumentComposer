package com.kpi.composer.exception;

public class DatasetParseException extends RuntimeException {

    public DatasetParseException() {
        super();
    }

    public DatasetParseException(String message) {
        super(message);
    }

    public DatasetParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
