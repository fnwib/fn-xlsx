package com.github.fnwib.plugin;

@FunctionalInterface
public interface ValueFormatter<T> {

	T convert(T value);
}
