package com.github.fnwib.read.convert;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.read.operation.Title;
import org.apache.poi.ss.usermodel.Row;

public interface ExcelConverter<T> {

    T convert(Title title, Row row) throws ExcelException;
}
