package com.github.fnwib.databing;

import org.apache.poi.ss.usermodel.Row;

public interface LineReader<T> {

    boolean match(Row row);

    T read(Row row);

    LineWriter<T> getLineWriter();
}
