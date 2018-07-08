package com.github.fnwib.databing.valuehandler;

@FunctionalInterface
public interface ValueFormatter<T> {

	T convert(T value);
}
