package com.github.fnwib.write;

import org.apache.poi.ss.usermodel.CellStyle;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ExcelWriter<T> {

    ExcelWriterImpl setCellStyle(CellStyle cellStyle);
    /**
     * 写入excel
     *
     * @param element
     */
    void write(T element);

    void write(List<T> list);

    /**
     * 写入excel 合并 cellIndexes这些列
     *
     * @param list
     * @param cellIndexes 要合并的列
     */
    void writeMergedRegion(List<T> list, List<Integer> cellIndexes);

    File write2File() throws IOException;

}
