package com.github.fnwib.convert;

public interface ExcelConversionService {

    boolean canConvert(Class<?> type);

    ExcelConverter<?> findConvert(Class<?> type);
}
