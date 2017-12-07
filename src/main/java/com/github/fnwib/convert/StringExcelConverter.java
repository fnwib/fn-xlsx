package com.github.fnwib.convert;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.parse.Title;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Arrays;
import java.util.List;

public class StringExcelConverter implements ExcelConverter<String> {


    private final boolean toSingleByte;

    private final boolean filterInsideSpace;

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

    @Override
    public List<CellText> writeValue(Object obj, Title title) throws ExcelException {
        String s = (String) obj;
        TitleDesc desc = title.getList().get(0);
        return Arrays.asList(new CellText(desc.getIndex(), s));
    }

}