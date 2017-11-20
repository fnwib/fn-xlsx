package com.github.fnwib.util;

import com.google.common.base.Joiner;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.text.Collator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueUtil {

    private static final Collator COLLATOR = Collator.getInstance(Locale.CHINA);


    private static final Pattern PATTERN = Pattern.compile("[\t\r\n]");

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

    /**
     * 1.将 \t \r \n 均替换为 \s
     * 2.去除所有(前的空格
     * 3.非(前的多个空格替换成一个
     *
     * @param value
     * @return
     */
    public static String filterInsideSpace(String value) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        Matcher m = PATTERN.matcher(value);
        value = m.replaceAll(" ");

        StringBuilder stringBuilder = new StringBuilder();
        int begin = -1;
        int end = -1;
        char[] chars = value.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            if (chars[i] == ' ') {
                if (begin == -1) {
                    begin = i;
                    end = i;
                } else {
                    end++;
                }
            } else {
                if (chars[i] != '(' && begin != -1 && end != -1) {
                    stringBuilder.append(' ');
                }
                begin = -1;
                end = -1;
                stringBuilder.append(chars[i]);
            }
        }
        return stringBuilder.toString();

    }

    public static String getValue(Cell cell, boolean toSingleByte, boolean filterInsideSpace) {
        if (cell == null) {
            return "";
        }
        String value = cell.getStringCellValue();
        if (StringUtils.isBlank(value)) {
            return "";
        }
        if (toSingleByte) {
            value = BCConvert.toSingleByte(value);
        }
        if (filterInsideSpace) {
            value = ValueUtil.filterInsideSpace(value);
        }
        return value.trim();
    }

}
