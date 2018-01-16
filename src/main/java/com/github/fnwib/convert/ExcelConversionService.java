package com.github.fnwib.convert;

import com.fasterxml.jackson.databind.JavaType;

public interface ExcelConversionService {

    boolean canConvert(JavaType type);

    ExcelConverter<?> findConvert(JavaType type);
}
