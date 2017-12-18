package com.github.fnwib.reflect;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

@Getter
@AllArgsConstructor
public class Property {

    private final Field field;

    private final Method readMethod;

    private final Method writeMethod;

    public String getName() {
        if (field == null) {
            return null;
        } else {
            return field.getName();
        }
    }

}
