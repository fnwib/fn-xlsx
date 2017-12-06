package com.github.fnwib.read.convert;

public interface ExcelConversionService {

    boolean canConvert(Class<?> type);

    ExcelConverter<?> findConvert(Class<?> type);
}
