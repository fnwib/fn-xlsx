package com.github.fnwib.convert;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.parse.Title;
import com.github.fnwib.write.CellText;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public interface ExcelConverter<T> {

    /**
     * 默认值
     */
    T getDefaultValue();

    /**
     * 读
     *
     * @param title
     * @param row
     * @return
     * @throws ExcelException
     */
    T convert(Title title, Row row) throws ExcelException;

    /**
     * 写
     *
     * @param t     值
     * @param title 规则
     * @throws ExcelException
     */
    List<CellText> writeValue(Object t, Title title) throws ExcelException;
}
