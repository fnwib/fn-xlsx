package com.github.fnwib.convert;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.parse.Title;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TitleDescMapExcelConverter implements ExcelConverter<Map<TitleDesc, String>> {

    boolean toSingleByte;

    boolean filterInsideSpace;

    public TitleDescMapExcelConverter() {
        this.toSingleByte = true;
        this.filterInsideSpace = false;
    }

    public TitleDescMapExcelConverter(boolean toSingleByte, boolean filterInsideSpace) {
        this.toSingleByte = toSingleByte;
        this.filterInsideSpace = filterInsideSpace;
    }

    @Override
    public Map<TitleDesc, String> convert(Title title, Row row) {
        Map<TitleDesc, String> hashMap = new HashMap<>();
        for (TitleDesc titleDesc : title.getList()) {
            Cell cell = row.getCell(titleDesc.getIndex());
            hashMap.put(titleDesc, ValueUtil.getValue(cell, toSingleByte, filterInsideSpace));
        }
        return hashMap;
    }


    @Override
    public List<CellText> writeValue(Object obj, Title title) throws ExcelException {
        Map<TitleDesc, String> map = (Map<TitleDesc, String>) obj;
        Map<Integer, String> newMap = new HashMap<>();
        map.forEach((titleDesc, s) -> newMap.put(titleDesc.getIndex(), s));
        List<TitleDesc> list = title.getList();
        List<CellText> result = new ArrayList<>(list.size());
        for (TitleDesc desc : list) {
            String s = newMap.get(desc.getIndex());
            CellText text = new CellText(desc.getIndex(), s);
            result.add(text);
        }
        return result;
    }

}