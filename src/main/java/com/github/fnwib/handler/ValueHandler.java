package com.github.fnwib.handler;

@FunctionalInterface
public interface ValueHandler<T> {

    T convert(T param);
}
