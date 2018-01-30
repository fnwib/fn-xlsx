package com.github.fnwib.databing.title;

import com.google.common.base.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Sequence sequence = (Sequence) o;
        return Objects.equal(value, sequence.value);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(value);
    }
}
