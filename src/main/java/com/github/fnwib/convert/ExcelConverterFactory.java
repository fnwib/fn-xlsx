package com.github.fnwib.convert;

public interface ExcelConverterFactory<R> {

    <T extends R> ExcelConverter<T> getConverter(Class<T> targetType);

}
