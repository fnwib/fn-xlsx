package com.github.fnwib.databing;

import org.apache.poi.ss.usermodel.Row;

import java.util.Map;

public interface LineReader<T> {

    boolean match(Row row);

    T convert(Row row);

    Map<String, Object> convertToMap(Row row);


    LineWriter<T> getLineWriter();
}
