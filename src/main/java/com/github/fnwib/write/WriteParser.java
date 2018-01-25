package com.github.fnwib.write;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

public interface WriteParser<T> {

    void setSheet(Sheet sheet);

    void setCellStyle(CellStyle defaultCellStyle);

    void convert(int rowNum, T element);

    void convert(int rowNum, List<T> elements, List<Integer> mergedRangeIndexes);

}
