package com.github.fnwib.databing;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public interface LineWriter<T> {

    void setSheet(Sheet sheet);

    void setCellStyle(CellStyle defaultCellStyle);

    void convert(int rowNum, T element);

    void convert(int rowNum, List<T> elements, List<Integer> mergedRangeIndexes);

}
