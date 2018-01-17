package com.github.fnwib.convert;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.handler.ValueHandler;
import com.github.fnwib.parse.Title;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitleKeyMapExcelConverter implements ExcelConverter<Map<String, String>> {

    private final List<ValueHandler<String>> valueHandlers;

    public TitleKeyMapExcelConverter() {
        this.valueHandlers = Collections.emptyList();
    }

    public TitleKeyMapExcelConverter(List<ValueHandler<String>> valueHandlers) {
        this.valueHandlers = valueHandlers;
    }

    public TitleKeyMapExcelConverter(ValueHandler<String>... valueHandlers) {
        this.valueHandlers = Lists.newArrayList(valueHandlers);
    }

    @Override
    public Map<String, String> getDefaultValue() {
        return Maps.newHashMap();
    }

    @Override
    public Map<String, String> convert(Title title, Row row) {
        Map<String, String> hashMap = new HashMap<>();
        for (TitleDesc titleDesc : title.getList()) {
            Cell cell = row.getCell(titleDesc.getIndex());
            String value = ValueUtil.getCellValue(cell, valueHandlers);
            hashMap.put(titleDesc.getTitle(), value);
        }
        return hashMap;
    }

    @Override
    public List<CellText> writeValue(Object obj, Title title) throws ExcelException {
        throw new NotSupportedException("Map<Integer,String>类型不支持写操作 请使用 Map<TitleDesc,String>");
    }

}