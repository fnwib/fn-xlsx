package com.github.fnwib.write;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface ExcelWriter<T> {

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

    /**
     * 1.写出excel
     * 2.清理临时文件
     *
     * @throws IOException
     */
    void flush() throws IOException;

    /**
     * @return
     */
    List<File> getFiles();

}
