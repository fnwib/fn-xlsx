package com.github.fnwib.reflect;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

@Getter
@AllArgsConstructor
public class Property {

    private final Field field;

    private final PropertyDescriptor propertyDescriptor;

    public String getName() {
        if (field == null) {
            return null;
        } else {
            return field.getName();
        }
    }

}
