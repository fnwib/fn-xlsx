package com.github.fnwib.databing.convert;

import org.apache.poi.ss.usermodel.Row;

public interface ReadConverter {

    String getKey();

    Object getValue(Row row);
}
