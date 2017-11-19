package com.github.fnwib.operation;

import org.apache.poi.ss.usermodel.Row;

public interface Operator<T> {

    boolean match(Row row);

    T convert(Row element);

}
