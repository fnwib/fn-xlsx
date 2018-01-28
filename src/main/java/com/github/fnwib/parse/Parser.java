package com.github.fnwib.parse;

import com.github.fnwib.read.ReadParser;
import com.github.fnwib.write.WriteParser;
import org.apache.poi.ss.usermodel.Row;

@Deprecated
public interface Parser<T> {

    Class<T> getClazz();

    /**
     * 判定是否excel的头
     *
     * @param row
     * @return
     */
    boolean match(Row row);

    ReadParser<T> createReadParser();

    WriteParser<T> createWriteParser();

}
