package com.kpi.composer.exception;


public class ExpressionParseException extends RuntimeException {

    public ExpressionParseException() {
        super();
    }

    public ExpressionParseException(String message) {
        super(message);
    }

    public ExpressionParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
