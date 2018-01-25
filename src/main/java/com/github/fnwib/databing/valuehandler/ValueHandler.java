package com.github.fnwib.databing.valuehandler;

/**
 * 处理读取的cell字符串
 */
@FunctionalInterface
public interface ValueHandler<T> {

    T convert(T value);
}
