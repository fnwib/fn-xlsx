package com.github.fnwib.databing.ser;

import java.util.Optional;

@FunctionalInterface
public interface Serializer {

    String serialize(Optional<?> value);
}
