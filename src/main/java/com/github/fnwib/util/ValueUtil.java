package com.github.fnwib.util;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.text.Collator;
import java.util.*;

@Slf4j
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

    public static String getCellValue(Cell cell, ValueHandler<String>... valueHandlers) {
        return getCellValue(cell, Lists.newArrayList(valueHandlers));
    }

    public static String getCellValue(Cell cell, Collection<ValueHandler<String>> valueHandlers) {
        if (cell == null) {
            return "";
        }
        final String value = cell.getStringCellValue();
        return getStringValue(value, valueHandlers);
    }

    public static String getStringValue(final String value, Collection<ValueHandler<String>> valueHandlers) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String temp = value;
        for (ValueHandler<String> valueHandler : valueHandlers) {
            temp = valueHandler.convert(temp);
        }
        return temp;
    }

    public static Optional<String> substringBetween(final String text, final String prefix, final String suffix) {
        if (text == null || prefix == null || suffix == null) {
            return Optional.empty();
        }
        if (text.startsWith(prefix) && text.endsWith(suffix)) {
            final String root;
            if (prefix.equals("") && suffix.equals("")) {
                root = text;
            } else if (prefix.equals("") && !suffix.equals("")) {
                root = text.substring(0, text.length() - suffix.length());
            } else if (!prefix.equals("") && suffix.equals("")) {
                root = text.substring(prefix.length());
            } else {
                root = StringUtils.substringBetween(text, prefix, suffix);
            }
            return Optional.of(root);
        } else {
            return Optional.empty();
        }
    }

    public static void main(String[] args) {
        Optional<String> number = substringBetween("Number", "", "");
        String s = number.orElse("numm   aasdas");
        System.out.println(s);
    }

}
