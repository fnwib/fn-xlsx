package com.github.fnwib.util;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ValueUtil {

    public static String getCellValue(Cell cell) {
        return getCellValue(cell, Collections.emptyList());
    }

    public static String getCellValue(Cell cell, Collection<ValueHandler> valueHandlers) {
        if (cell == null) {
            return "";
        }
        final String value = cell.getStringCellValue();
        return getStringValue(value, valueHandlers);
    }

    public static String getStringValue(final String value, Collection<ValueHandler> valueHandlers) {
        if (StringUtils.isBlank(value)) {
            return "";
        }
        String temp = value;
        for (ValueHandler valueHandler : valueHandlers) {
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

}
