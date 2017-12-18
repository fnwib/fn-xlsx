package com.github.fnwib.reflect;

import com.github.fnwib.exception.PropertyException;
import com.github.fnwib.util.Assert;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public enum BeanHelper {

    INSTANCE;

    private final Map<Class<?>, List<Property>> types = new ConcurrentHashMap();

    private List<Property> resolve(final Class<?> clazz) {
        Assert.isTrue(clazz != null, "参数不能为null");
        try {
            Field[] fields = clazz.getDeclaredFields();
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz)
                    .getPropertyDescriptors();
            List<Property> properties = new ArrayList<>(Math.max(fields.length, propertyDescriptors.length));
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                for (Field field : fields) {
                    if (field.getName().equalsIgnoreCase(descriptor.getName())) {
                        Property property = new Property(field, descriptor);
                        properties.add(property);
                    }
                }
            }
            return properties;
        } catch (IntrospectionException e) {
            throw new PropertyException(e);
        }
    }

    public List<Property> getProperties(final Class<?> clazz) {
        Assert.isTrue(clazz != null, "参数不能为null");
        if (types.containsKey(clazz)) {
            return types.get(clazz);
        } else {
            List<Property> resolve = resolve(clazz);
            types.put(clazz, resolve);
            return resolve;
        }
    }


    public List<Property> getPropertiesWithAnnotation(final Class<?> clazz, final Class<? extends Annotation> annotationCls) {
        Assert.isTrue(clazz != null && annotationCls != null, "参数不能为null");
        List<Property> properties = getProperties(clazz);
        return properties.stream()
                .filter(property -> property.getField().getAnnotation(annotationCls) != null).collect(Collectors.toList());

    }


}
