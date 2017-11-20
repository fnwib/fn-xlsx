package com.github.fnwib.convert;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.operation.Title;
import org.apache.poi.ss.usermodel.Row;

public interface ExcelConverter<T> {

    T convert(Title title, Row row) throws ExcelException;
}
