package com.github.fnwib.write;

import com.github.fnwib.databing.LineWriter;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

@Deprecated
public interface WriteParser<T> extends LineWriter<T> {

    void setSheet(Sheet sheet);

    void setCellStyle(CellStyle defaultCellStyle);

    void convert(int rowNum, T element);

    void convert(int rowNum, List<T> elements, List<Integer> mergedRangeIndexes);

}
