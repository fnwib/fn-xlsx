package com.github.fnwib.convert;


public interface ExcelConverterRegistry {


    void addConverter(ExcelConverter<?> converter);

    void addConverterFactory(ExcelConverterFactory<?> factory);

    @Deprecated
    void addConverter(Class<?> type, ExcelConverter<?> converter);

    @Deprecated
    void addConverterFactory(Class<?> type, ExcelConverterFactory<?> factory);

    @Deprecated
    void removeConvertible(Class<?> type);

}