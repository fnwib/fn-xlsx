package com.github.fnwib.read.convert;

public interface ExcelConverterFactory<R> {

    <T extends R> ExcelConverter<T> getConverter(Class<T> targetType);

}
