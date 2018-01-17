package com.github.fnwib.convert;


public interface ExcelConverterRegistry {


    void addConverter(ExcelConverter<?> converter);

    void addConverterFactory(ExcelConverterFactory<?> factory);

}