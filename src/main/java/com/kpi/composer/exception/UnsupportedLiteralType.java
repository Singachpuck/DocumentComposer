package com.kpi.composer.exception;

import lombok.Getter;

@Getter
public class UnsupportedLiteralType extends RuntimeException {

    private final Class<?> type;

    public UnsupportedLiteralType(Class<?> type) {
        super(type.getName());
        this.type = type;
    }


    public UnsupportedLiteralType(Class<?> type, Throwable cause) {
        super(type.getName(), cause);
        this.type = type;
    }
}
