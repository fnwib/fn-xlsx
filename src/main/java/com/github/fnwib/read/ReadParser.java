package com.github.fnwib.read;

import com.github.fnwib.exception.ExcelException;
import org.apache.poi.ss.usermodel.Row;
@Deprecated
public interface ReadParser<T> {

    T convert(Row row) throws ExcelException;
}
