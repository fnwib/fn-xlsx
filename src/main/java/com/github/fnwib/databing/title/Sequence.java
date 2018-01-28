package com.github.fnwib.databing.title;

public class Sequence {

    private String value;

    public Sequence(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public Integer asInt() {
        return Integer.parseInt(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
