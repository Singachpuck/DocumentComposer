package com.kpi.composer.exception;

public class VariableCastException extends RuntimeException {

    public VariableCastException() {
        super();
    }

    public VariableCastException(String message) {
        super(message);
    }

    public VariableCastException(String message, Throwable cause) {
        super(message, cause);
    }
}
