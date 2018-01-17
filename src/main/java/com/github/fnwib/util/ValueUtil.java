package com.github.fnwib.util;

import com.github.fnwib.handler.ValueHandler;
import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.text.Collator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueUtil {

    private static final Collator COLLATOR = Collator.getInstance(Locale.CHINA);

    /**
     * 排序并去空格
     * 去重复数据
     *
     * @param s
     * @param symbols
     * @return
     */
    public static String sortAndTrim(String s, String symbols) {
        if (s == null) {
            return null;
        }
        if (s.contains(symbols)) {
            String[] ps = s.split(symbols);
            Set<String> set = new TreeSet<>(COLLATOR);
            for (String p : ps) {
                set.add(p.trim());
            }
            return Joiner.on(symbols).join(set);
        } else {
            return s;
        }
    }

    public static String getCellValue(Cell cell, List<ValueHandler<String>> valueHandlers) {
        if (cell == null) {
            return "";
        }
        final String value = cell.getStringCellValue();
        return getStringValue(value, valueHandlers);
    }

    public static String getStringValue(final String value, List<ValueHandler<String>> valueHandlers) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String temp = value;
        for (ValueHandler<String> valueHandler : valueHandlers) {
            temp = valueHandler.convert(temp);
        }
        return temp;
    }

}
