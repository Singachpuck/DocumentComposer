package com.kpi.composer.exception;

public class DocumentComposingException extends RuntimeException {

    public DocumentComposingException(String message) {
        super(message);
    }

    public DocumentComposingException(String message, Throwable cause) {
        super(message, cause);
    }
}
