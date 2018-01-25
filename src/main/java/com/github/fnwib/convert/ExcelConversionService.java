package com.github.fnwib.convert;

import com.fasterxml.jackson.databind.JavaType;
@Deprecated
public interface ExcelConversionService {

    boolean canConvert(JavaType type);

    ExcelConverter<?> findConvert(JavaType type);
}
