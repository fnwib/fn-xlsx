package com.github.fnwib.write;

import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

@Slf4j
public class WriteParserNewImpl<T> implements WriteParser<T> {

    private Sheet sheet;

    private CellStyle cellStyle;

    @Override
    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void setCellStyle(CellStyle defaultCellStyle) {
        this.cellStyle = defaultCellStyle;
    }


    @Override
    public void convert(int rowNum, T element) {

    }

    @Override
    public void convert(int rowNum, List<T> elements, List<Integer> mergedRangeIndexes) {

    }
}
