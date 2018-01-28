package com.github.fnwib.databing.convert;

public interface PropertyConverter extends WriteConverter, ReadConverter {

    boolean isMatched();
}
