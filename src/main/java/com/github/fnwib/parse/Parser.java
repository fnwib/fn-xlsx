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

    /**
     * @return
     */
    ReadParser<T> createReadParser();

    WriteParser<T> createWriteParser();

}
