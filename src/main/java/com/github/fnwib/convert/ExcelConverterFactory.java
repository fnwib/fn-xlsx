package com.github.fnwib.convert;

import com.fasterxml.jackson.databind.JavaType;
@Deprecated
public interface ExcelConverterFactory<R> {

    <T extends R> ExcelConverter<T> getConverter(JavaType type);

}
