package com.github.fnwib.databing.deser;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.deser.CellDeserializer;
import com.github.fnwib.reflect.BeanResolver;
import com.google.common.collect.Maps;

import java.util.Map;

public class DeserializerConfig {

        private Map<JavaType, CellDeserializer<?>> deserializers = Maps.newConcurrentMap();

        public void register(CellDeserializer<?> cellDeserializer) {
            JavaType genericType = BeanResolver.getInterfaceGenericType(cellDeserializer.getClass());
            this.deserializers.put(genericType, cellDeserializer);
        }

        public CellDeserializer<?> findCellDeserializer(JavaType javaType) {
            return deserializers.get(javaType);
        }
    }