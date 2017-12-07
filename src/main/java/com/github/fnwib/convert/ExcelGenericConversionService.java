package com.github.fnwib.convert;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ExcelGenericConversionService implements ExcelConversionService, ExcelConverterRegistry {

    private final Map<Class<?>, ExcelConverter> CONVERTERS_MAP = new ConcurrentHashMap<>();

    private final Map<Class<?>, ExcelConverterFactory> CONVERTERS_FACTORY_MAP = new ConcurrentHashMap<>();

    @Override
    public boolean canConvert(Class<?> type) {
        return findConvert(type) != null;
    }

    @Override
    public ExcelConverter<?> findConvert(Class<?> type) {
        if (CONVERTERS_MAP.containsKey(type)) {
            return CONVERTERS_MAP.get(type);
        } else {
            for (Map.Entry<Class<?>, ExcelConverterFactory> entry : CONVERTERS_FACTORY_MAP.entrySet()) {
                Class<?> key = entry.getKey();
                if (key.isAssignableFrom(type)) {
                    ExcelConverter converter = entry.getValue().getConverter(type);
                    CONVERTERS_MAP.put(type, converter);
                    return entry.getValue().getConverter(type);
                }
            }
        }

        return null;
    }

    @Override
    public void addConverter(Class<?> type, ExcelConverter<?> converter) {
        CONVERTERS_MAP.put(type, converter);
    }

    @Override
    public void addConverterFactory(Class<?> type, ExcelConverterFactory<?> factory) {
        CONVERTERS_FACTORY_MAP.put(type, factory);
    }

    @Override
    public void removeConvertible(Class<?> type) {
        CONVERTERS_MAP.remove(type);
    }


}

