package com.kpi.composer.exception;

public class VariableNotFoundException extends RuntimeException {

    public VariableNotFoundException() {
        super();
    }

    public VariableNotFoundException(String message) {
        super(message);
    }

    public VariableNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
