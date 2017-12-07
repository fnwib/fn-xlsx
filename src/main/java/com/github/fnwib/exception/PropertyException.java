package com.github.fnwib.exception;

public class PropertyException extends RuntimeException {

    public PropertyException() {
        super();
    }

    public PropertyException(String msg) {
        super(msg);
    }

    public PropertyException(Exception e) {
        super(e);
    }

    public PropertyException(String msg, Exception e) {
        super(msg, e);
    }
}