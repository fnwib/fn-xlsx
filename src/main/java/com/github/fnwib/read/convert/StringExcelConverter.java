package com.github.fnwib.read.convert;

import com.github.fnwib.read.operation.Title;
import com.github.fnwib.read.operation.TitleDesc;
import com.github.fnwib.util.ValueUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;

public class StringExcelConverter implements ExcelConverter<String> {


    boolean toSingleByte;

    boolean filterInsideSpace;

    public StringExcelConverter() {
        this.toSingleByte = true;
        this.filterInsideSpace = false;
    }

    public StringExcelConverter(boolean toSingleByte, boolean filterInsideSpace) {
        this.toSingleByte = toSingleByte;
        this.filterInsideSpace = filterInsideSpace;
    }

    @Override
    public String convert(Title title, Row row) {
        List<TitleDesc> list = title.getList();
        if (list.size() != 1) {
            return null;
        }
        TitleDesc titleDesc = list.get(0);
        Cell cell = row.getCell(titleDesc.getIndex());
        return ValueUtil.getValue(cell, toSingleByte, filterInsideSpace);
    }
}