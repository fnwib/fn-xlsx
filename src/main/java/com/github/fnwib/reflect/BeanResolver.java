package com.github.fnwib.reflect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.exception.PropertyException;
import com.github.fnwib.exception.SettingException;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public enum BeanResolver {

    INSTANCE;

    public static final TypeFactory typeFactory = TypeFactory.defaultInstance();

    private final Map<Class<?>, List<Property>> types = new ConcurrentHashMap<>();

    public static JavaType getInterfaceGenericType(Class<?> clazz) {
        Type type = clazz.getGenericInterfaces()[0];
        TypeBindings bindings = typeFactory.constructType(type).getBindings();
        JavaType boundType = bindings.getBoundType(0);
        if (boundType != null) {
            return typeFactory.constructType(boundType);
        } else {
            throw new SettingException();
        }
    }


    private synchronized List<Property> resolve(final Class<?> clazz) {
        if (clazz ==null){
            throw new IllegalArgumentException("参数不能为null");
        }
        try {
            Field[] fields = clazz.getDeclaredFields();
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz)
                    .getPropertyDescriptors();
            List<Property> properties = new ArrayList<>(Math.max(fields.length, propertyDescriptors.length));
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                for (Field field : fields) {
                    if (field.getName().equalsIgnoreCase(descriptor.getName())) {
                        Type genericType = field.getGenericType();
                        JavaType javaType = typeFactory.constructType(genericType);
                        Property property = new Property(field, javaType, descriptor);
                        properties.add(property);
                    }
                }
            }
            return properties;
        } catch (IntrospectionException e) {
            throw new PropertyException(e);
        }
    }

    public synchronized List<Property> getProperties(final Class<?> clazz) {
        if (clazz ==null){
            throw new IllegalArgumentException("参数不能为null");
        }
        if (types.containsKey(clazz)) {
            return types.get(clazz);
        } else {
            List<Property> resolve = resolve(clazz);
            types.put(clazz, resolve);
            return resolve;
        }
    }


    public List<Property> getPropertiesWithAnnotation(final Class<?> clazz, final Class<? extends Annotation> annotationCls) {
        if (clazz ==null || annotationCls == null){
            throw new IllegalArgumentException("参数不能为null");
        }
        List<Property> properties = getProperties(clazz);
        return properties.stream()
                .filter(property -> property.getField().getAnnotation(annotationCls) != null).collect(Collectors.toList());

    }


}
