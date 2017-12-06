package com.github.fnwib.read.operation;

import com.github.fnwib.exception.ExcelException;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Map;

public interface Operator<T> {
    /**
     * 判定是否excel的头
     *
     * @param row
     * @return
     */
    boolean match(Row row);

    /**
     * 返回改行与实体类字段的映射关系
     *
     * @param row
     * @return
     */
    Map<String, List<TitleDesc>> getTitles(Row row);

    /**
     * 如果已经找到头所在的行
     * <p>
     * 可以通过这个方法转换
     *
     * @param element
     * @return
     * @throws ExcelException
     */
    T convert(Row element) throws ExcelException;

}
