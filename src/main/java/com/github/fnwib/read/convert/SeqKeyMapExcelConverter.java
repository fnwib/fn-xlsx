package com.github.fnwib.read.convert;

import com.github.fnwib.read.operation.Title;
import com.github.fnwib.read.operation.TitleDesc;
import com.github.fnwib.util.ValueUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.HashMap;
import java.util.Map;

public class SeqKeyMapExcelConverter implements ExcelConverter<Map<Integer, String>> {

    boolean toSingleByte;

    boolean filterInsideSpace;

    public SeqKeyMapExcelConverter() {
        this.toSingleByte = true;
        this.filterInsideSpace = false;
    }

    public SeqKeyMapExcelConverter(boolean toSingleByte, boolean filterInsideSpace) {
        this.toSingleByte = toSingleByte;
        this.filterInsideSpace = filterInsideSpace;
    }

    @Override
    public Map<Integer, String> convert(Title title, Row row) {
        Map<Integer, String> hashMap = new HashMap<>();
        for (TitleDesc titleDesc : title.getList()) {
            Cell cell = row.getCell(titleDesc.getIndex());
            hashMap.put(titleDesc.getIndex() - title.getMinIndex(), ValueUtil.getValue(cell, toSingleByte, filterInsideSpace));
        }
        return hashMap;
    }

}