package com.github.fnwib.convert;

import com.fasterxml.jackson.databind.JavaType;

public interface ExcelConverterFactory<R> {

    <T extends R> ExcelConverter<T> getConverter(JavaType type);

}
