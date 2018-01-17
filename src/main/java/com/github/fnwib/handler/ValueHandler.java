package com.github.fnwib.handler;

/**
 * 处理读取的cell字符串
 *
 * @param <T>
 */
@FunctionalInterface
public interface ValueHandler<T> {

    T convert(T param);
}
