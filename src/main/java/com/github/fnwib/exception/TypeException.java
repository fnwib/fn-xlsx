package com.github.fnwib.exception;

public class TypeException extends RuntimeException {

    public TypeException() {
        super();
    }

    public TypeException(String msg) {
        super(msg);
    }

    public TypeException(Exception e) {
        super(e);
    }

    public TypeException(String msg, Exception e) {
        super(msg, e);
    }
}