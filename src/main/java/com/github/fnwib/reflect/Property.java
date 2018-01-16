package com.github.fnwib.reflect;

import com.fasterxml.jackson.databind.JavaType;
import lombok.Getter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;

@Getter
public class Property {

    private final Field              field;
    private final JavaType           javaType;
    private final PropertyDescriptor propertyDescriptor;


    public Property(Field field, JavaType javaType, PropertyDescriptor propertyDescriptor) {
        this.field = field;
        this.javaType = javaType;
        this.propertyDescriptor = propertyDescriptor;
    }

    public String getName() {
        if (field == null) {
            return null;
        } else {
            return field.getName();
        }
    }

}
