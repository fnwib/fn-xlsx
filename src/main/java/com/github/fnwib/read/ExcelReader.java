package com.github.fnwib.read;


import com.github.fnwib.exception.ExcelException;

import java.util.List;

/**
 * 可以自行扩展方法
 *
 * @param <T>
 */
public interface ExcelReader<T> {

    String getPreTitle(int rowNum, int cellNum);

    boolean findTitle();

    /**
     * 只在前几行查找title
     *
     * @param num
     * @return
     */
    boolean findTitle(int num);

    List<T> getData() throws ExcelException;

}
