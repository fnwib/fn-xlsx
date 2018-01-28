package com.github.fnwib.convert;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.parse.Title;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
@Deprecated
public class StringExcelConverter implements ExcelConverter<String> {

    private final List<ValueHandler> valueHandlers;

    public StringExcelConverter() {
        this.valueHandlers = Collections.emptyList();
    }

    public StringExcelConverter(List<ValueHandler> valueHandlers) {
        this.valueHandlers = valueHandlers;
    }

    public StringExcelConverter(ValueHandler... valueHandlers) {
        this.valueHandlers = Lists.newArrayList(valueHandlers);
    }

    public List<ValueHandler> getValueHandlers() {
        return valueHandlers;
    }

    @Override
    public String getDefaultValue() {
        return null;
    }

    @Override
    public String convert(Title title, Row row) {
        List<TitleDesc> list = title.getList();
        if (list.size() != 1) {
            return null;
        }
        TitleDesc titleDesc = list.get(0);
        Cell cell = row.getCell(titleDesc.getIndex());
        return ValueUtil.getCellValue(cell, valueHandlers);
    }

    @Override
    public List<CellText> writeValue(Object obj, Title title) throws ExcelException {
        String s = (String) obj;
        TitleDesc desc = title.getList().get(0);
        return Arrays.asList(new CellText(desc.getIndex(), s));
    }

}