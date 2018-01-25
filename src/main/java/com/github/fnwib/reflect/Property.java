package com.github.fnwib.reflect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import lombok.Getter;
import org.apache.poi.ss.formula.functions.T;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
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

    public <T extends Annotation> T getAnnotation(final Class<T> annotationCls) {
        return field.getAnnotation(annotationCls);
    }

}
