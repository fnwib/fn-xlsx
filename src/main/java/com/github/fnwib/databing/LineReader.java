package com.github.fnwib.databing;

import org.apache.poi.ss.usermodel.Row;

import java.util.Map;
import java.util.Optional;

public interface LineReader<T> {

    boolean isEmpty(Row row);

    boolean match(Row row);

    Optional<T> convert(Row row);

    Map<String, Object> convertToMap(Row row);

    LineWriter<T> getLineWriter();
}
