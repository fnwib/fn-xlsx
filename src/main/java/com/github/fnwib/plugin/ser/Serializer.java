package com.github.fnwib.plugin.ser;

import java.util.Optional;

/**
 * 对象序列化 方法
 */
@FunctionalInterface
public interface Serializer {

    String serialize(Optional<?> value);
}
