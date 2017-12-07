package com.github.fnwib.parse;

import com.github.fnwib.read.ReadParser;
import com.github.fnwib.write.WriteParser;
import org.apache.poi.ss.usermodel.Row;

public interface Parser<T> {
    /**
     * 判定是否excel的头
     *
     * @param row
     * @return
     */
    boolean match(Row row);

//    /**
//     * 返回改行与实体类字段的映射关系
//     *
//     * @param row
//     * @return
//     */
//    Map<String, List<TitleDesc>> getTitles(Row row);

//    /**
//     * 如果已经找到头所在的行
//     * <p>
//     * 可以通过这个方法转换
//     *
//     * @param element
//     * @return
//     * @throws ExcelException
//     */
//    T convert(Row element) throws ExcelException;

    ReadParser<T> createReadParser();

    WriteParser<T> createWriteParser();

}
