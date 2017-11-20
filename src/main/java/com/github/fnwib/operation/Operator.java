package com.github.fnwib.operation;

import com.github.fnwib.exception.ExcelException;
import org.apache.poi.ss.usermodel.Row;

public interface Operator<T> {

    boolean match(Row row);

    T convert(Row element) throws ExcelException;

}
