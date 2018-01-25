package com.github.fnwib.databing;

import org.apache.poi.ss.usermodel.Row;

public interface RowParser<T> {


    boolean match(Row row);

    T convert(Row row);


}
