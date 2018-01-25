package com.github.fnwib.convert;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.reflect.BeanResolver;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
@Deprecated
public class ExcelGenericConversionService implements ExcelConversionService, ExcelConverterRegistry {

    private final Map<JavaType, ExcelConverter> CONVERTERS_MAP = new ConcurrentHashMap<>();

    private final Map<JavaType, ExcelConverterFactory> CONVERTERS_FACTORY_MAP = new ConcurrentHashMap<>();

    @Override
    public boolean canConvert(JavaType type) {
        return findConvert(type) != null;
    }

    @Override
    public ExcelConverter<?> findConvert(JavaType type) {
        if (CONVERTERS_MAP.containsKey(type)) {
            return CONVERTERS_MAP.get(type);
        } else {
            for (Map.Entry<JavaType, ExcelConverterFactory> entry : CONVERTERS_FACTORY_MAP.entrySet()) {
                JavaType key = entry.getKey();
                if (type.isTypeOrSubTypeOf(key.getRawClass())) {
                    ExcelConverter converter = entry.getValue().getConverter(type);
                    CONVERTERS_MAP.put(type, converter);
                    return entry.getValue().getConverter(type);
                }
            }
        }

        return null;
    }

    @Override
    public void addConverter(ExcelConverter<?> converter) {
        JavaType genericType = BeanResolver.getInterfaceGenericType(converter.getClass());
        CONVERTERS_MAP.put(genericType, converter);
    }

    @Override
    public void addConverterFactory(ExcelConverterFactory<?> factory) {
        JavaType genericType = BeanResolver.getInterfaceGenericType(factory.getClass());
        CONVERTERS_FACTORY_MAP.put(genericType, factory);
    }

}

