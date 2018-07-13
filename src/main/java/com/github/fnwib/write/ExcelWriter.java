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

    void write(List<T> elements);

    /**
     * 写入excel 合并 cellIndexes这些列
     *
     * @param elements
     * @param mergeIndexes 要合并的列
     */
    void writeMergedRegion(List<T> elements, List<Integer> mergeIndexes);

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
