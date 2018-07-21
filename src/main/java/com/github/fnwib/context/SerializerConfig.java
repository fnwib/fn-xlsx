package com.github.fnwib.context;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.plugin.ser.Serializer;
import com.github.fnwib.reflect.BeanResolver;
import com.google.common.collect.Maps;

import java.util.Map;

public class SerializerConfig {

    private Map<JavaType, Serializer> serializers = Maps.newConcurrentMap();

    public void register(Serializer serializer) {
        JavaType genericType = BeanResolver.getInterfaceGenericType(serializer.getClass());
        this.serializers.put(genericType, serializer);
    }

    public Serializer findSerializer(JavaType javaType) {
        return serializers.get(javaType);
    }
}