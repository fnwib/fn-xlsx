package com.github.fnwib.exception;

public class ParseException extends RuntimeException {

    public ParseException(String s, int errorOffset) {
        super(s);
        this.errorOffset = errorOffset;
    }


    public int getErrorOffset() {
        return errorOffset;
    }

    private int errorOffset;
}