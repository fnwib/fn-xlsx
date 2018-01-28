package com.github.fnwib.reflect;

import com.fasterxml.jackson.databind.JavaType;
import lombok.Getter;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

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

    public <T extends Annotation> T getAnnotation(final Class<T> annotationCls) {
        return field.getAnnotation(annotationCls);
    }

    public Method getReadMethod() {
        return propertyDescriptor.getReadMethod();
    }

    public JavaType getKeyType() {
        return javaType.getKeyType();
    }

    public JavaType getContentType() {
        return javaType.getContentType();
    }


}
