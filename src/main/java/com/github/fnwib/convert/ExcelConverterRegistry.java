package com.github.fnwib.convert;

@Deprecated
public interface ExcelConverterRegistry {


    void addConverter(ExcelConverter<?> converter);

    void addConverterFactory(ExcelConverterFactory<?> factory);

}