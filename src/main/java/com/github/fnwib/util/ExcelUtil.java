package com.github.fnwib.util;

import org.apache.commons.lang3.StringUtils;

public class ExcelUtil {


    /**
     * Excel column index begin 1
     *
     * @param colStr
     * @return
     */
    public static int column2Num(String colStr) {
        if (StringUtils.isBlank(colStr)) {
            throw new IllegalArgumentException("colStr must be not blank");
        }
        final String column = colStr.trim();
        int length = column.length();
        int num;
        int result = 0;
        for (int i = 0; i < length; i++) {
            char ch = colStr.charAt(length - i - 1);
            num = (ch - 'A' + 1);
            num *= Math.pow(26, i);
            result += num;
        }
        return result;
    }

    /**
     * Excel column index begin 1
     *
     * @param columnIndex
     * @return
     */
    public static String num2Column(int columnIndex) {
        if (columnIndex <= 0) {
            return null;
        }
        String columnStr = "";
        columnIndex--;
        do {
            if (columnStr.length() > 0) {
                columnIndex--;
            }
            columnStr = ((char) (columnIndex % 26 + (int) 'A')) + columnStr;
            columnIndex = ((columnIndex - columnIndex % 26) / 26);
        } while (columnIndex > 0);
        return columnStr;
    }
}
