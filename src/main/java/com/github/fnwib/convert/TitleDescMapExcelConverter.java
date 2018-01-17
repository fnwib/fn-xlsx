package com.github.fnwib.convert;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.handler.ValueHandler;
import com.github.fnwib.parse.Title;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

public class TitleDescMapExcelConverter implements ExcelConverter<Map<TitleDesc, String>> {

    private final List<ValueHandler<String>> valueHandlers;

    public TitleDescMapExcelConverter() {
        this.valueHandlers = Collections.emptyList();
    }

    @Deprecated
    public TitleDescMapExcelConverter(boolean toSingleByte, boolean filterInsideSpace) {
        this.valueHandlers = Collections.emptyList();
    }

    public TitleDescMapExcelConverter(List<ValueHandler<String>> valueHandlers) {
        this.valueHandlers = valueHandlers;
    }

    public TitleDescMapExcelConverter(ValueHandler<String>... valueHandlers) {
        this.valueHandlers = Lists.newArrayList(valueHandlers);
    }


    @Override
    public Map<TitleDesc, String> getDefaultValue() {
        return Maps.newHashMap();
    }

    @Override
    public Map<TitleDesc, String> convert(Title title, Row row) {
        Map<TitleDesc, String> hashMap = new HashMap<>();
        for (TitleDesc titleDesc : title.getList()) {
            Cell cell = row.getCell(titleDesc.getIndex());
            String value = ValueUtil.getCellValue(cell, valueHandlers);
            hashMap.put(titleDesc, value);
        }
        return hashMap;
    }


    @Override
    public List<CellText> writeValue(Object obj, Title title) throws ExcelException {
        List<TitleDesc> list = title.getList();
        Map<TitleDesc, String> map = (Map<TitleDesc, String>) obj;
        Map<Integer, String> newIndexMap = new HashMap<>(list.size());
        Map<String, String> newTitleMap = new HashMap<>(list.size());
        if (map != null) {
            map.forEach((titleDesc, s) -> newIndexMap.put(titleDesc.getIndex(), s));
            map.forEach((titleDesc, s) -> newTitleMap.put(titleDesc.getTitle(), s));
        }
        List<CellText> result = new ArrayList<>(list.size());
        for (TitleDesc desc : list) {
            String s = newTitleMap.get(desc.getTitle());
            if (s == null) {
                s = newIndexMap.get(desc.getIndex());
            }
            CellText text = new CellText(desc.getIndex(), s);
            result.add(text);
        }
        return result;
    }

}