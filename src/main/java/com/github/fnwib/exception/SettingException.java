package com.github.fnwib.exception;

public class SettingException extends RuntimeException {

    public SettingException() {
        super();
    }

    public SettingException(String msg) {
        super(msg);
    }

    public SettingException(Exception e) {
        super(e);
    }

    public SettingException(String msg, Exception e) {
        super(msg, e);
    }
}