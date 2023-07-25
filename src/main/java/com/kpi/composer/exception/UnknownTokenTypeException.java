package com.kpi.composer.exception;

public class UnknownTokenTypeException extends RuntimeException {

    private final Class<?> type;

    public UnknownTokenTypeException(Class<?> type) {
        super(type.getName());
        this.type = type;
    }


    public UnknownTokenTypeException(Class<?> type, Throwable cause) {
        super(type.getName(), cause);
        this.type = type;
    }
}
