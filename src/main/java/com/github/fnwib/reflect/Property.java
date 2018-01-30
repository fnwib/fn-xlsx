package com.github.fnwib.reflect;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.ReadValueHandler;
import com.github.fnwib.databing.title.TitleMatcher;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.google.common.collect.Lists;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class Property {

    private final Field              field;
    private final JavaType           javaType;
    private final PropertyDescriptor propertyDescriptor;

    public Property(Field field, JavaType javaType, PropertyDescriptor propertyDescriptor) {
        Objects.requireNonNull(field);
        Objects.requireNonNull(javaType);
        Objects.requireNonNull(propertyDescriptor);
        this.field = field;
        this.javaType = javaType;
        this.propertyDescriptor = propertyDescriptor;
    }

    public Optional<TitleMatcher> getTitleMatcher() {
        CellType cellType = field.getAnnotation(CellType.class);
        AutoMapping mapping = field.getAnnotation(AutoMapping.class);
        if (mapping != null) {
            return Optional.of(new TitleMatcher(mapping));
        } else {
            if (cellType != null) {
                return Optional.of(new TitleMatcher(cellType));
            }
        }
        return Optional.empty();
    }

    public Collection<ValueHandler> getValueHandlers() {
        ReadValueHandler handler = field.getAnnotation(ReadValueHandler.class);
        if (handler == null) {
            return Collections.emptyList();
        }
        Collection<ValueHandler> valueHandlers = Lists.newArrayListWithCapacity(handler.value().length);
        for (Class<? extends ValueHandler> h : handler.value()) {
            Constructor<?>[] constructors = h.getConstructors();
            if (constructors.length == 1) {
                Constructor<?> constructor = constructors[0];
                try {
                    ValueHandler valueHandler = (ValueHandler) constructor.newInstance();
                    valueHandlers.add(valueHandler);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new SettingException(h.getName() + " no found non args constructor");
                }
            } else {
                throw new SettingException(h.getName() + " not support multi args constructor");
            }
        }
        return valueHandlers;
    }

    public Field getField() {
        return field;
    }

    public JavaType getJavaType() {
        return javaType;
    }

    public PropertyDescriptor getPropertyDescriptor() {
        return propertyDescriptor;
    }

    public JavaType getContentType() {
        return javaType.getContentType();
    }

    public String getName() {
        return field.getName();
    }

    public <T extends Annotation> T getAnnotation(final Class<T> annotationCls) {
        return field.getAnnotation(annotationCls);
    }

    public Method getReadMethod() {
        return propertyDescriptor.getReadMethod();
    }



}
