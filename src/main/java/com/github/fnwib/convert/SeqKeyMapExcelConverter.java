package com.github.fnwib.convert;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
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
@Deprecated
public class SeqKeyMapExcelConverter implements ExcelConverter<Map<Integer, String>> {

    private final List<ValueHandler> valueHandlers;

    public SeqKeyMapExcelConverter() {
        this.valueHandlers = Collections.emptyList();
    }

    public SeqKeyMapExcelConverter(List<ValueHandler> valueHandlers) {
        this.valueHandlers = valueHandlers;
    }

    public SeqKeyMapExcelConverter(ValueHandler... valueHandlers) {
        this.valueHandlers = Lists.newArrayList(valueHandlers);
    }


    @Override
    public Map<Integer, String> getDefaultValue() {
        return Maps.newHashMap();
    }

    @Override
    public Map<Integer, String> convert(Title title, Row row) {
        Map<Integer, String> hashMap = new HashMap<>();
        for (TitleDesc titleDesc : title.getList()) {
            Cell cell = row.getCell(titleDesc.getIndex());
            String value = ValueUtil.getCellValue(cell, valueHandlers);
            hashMap.put(titleDesc.getIndex() - title.getMinIndex(), value);
        }
        return hashMap;
    }


    @Override
    public List<CellText> writeValue(Object obj, Title title) throws ExcelException {
        throw new NotSupportedException("Map<Integer,String>类型不支持写操作 请使用 Map<TitleDesc,String>");
    }

}