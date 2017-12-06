package com.github.fnwib.read.convert;


public interface ExcelConverterRegistry {


    void addConverter(Class<?> type, ExcelConverter<?> converter);

    void addConverterFactory(Class<?> type, ExcelConverterFactory<?> factory);

    void removeConvertible(Class<?> targetType);

}