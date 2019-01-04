package com.github.fnwib.util;

import com.github.fnwib.plugin.ValueHandler;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

public class ValueUtil {

	private ValueUtil() {
	}

	public static Optional<String> getCellValue(Cell cell) {
		return getCellValue(cell, Collections.emptyList());
	}

	public static Optional<String> getCellValue(Cell cell, Collection<ValueHandler> valueHandlers) {
		if (cell == null) {
			return Optional.empty();
		}
		final String value = cell.getStringCellValue();
		return getStringValue(value, valueHandlers);
	}

	public static Optional<String> getStringValue(final String value, Collection<ValueHandler> valueHandlers) {
		if (StringUtils.isBlank(value)) {
			return Optional.empty();
		}
		String temp = value;
		for (ValueHandler valueHandler : valueHandlers) {
			temp = valueHandler.convert(temp);
		}
		return Optional.ofNullable(temp);
	}

	public static Optional<String> substringBetweenIgnoreCase(final String text, final String prefix, final String suffix) {
		if (text == null || prefix == null || suffix == null) {
			return Optional.empty();
		}
		return substringBetweenNotNull(text.toLowerCase(), prefix.toLowerCase(), suffix.toLowerCase());
	}


	private static Optional<String> substringBetweenNotNull(final String text, final String prefix, final String suffix) {
		if (text.startsWith(prefix) && text.endsWith(suffix)) {
			final String root;
			if (StringUtils.EMPTY.equals(prefix) && StringUtils.EMPTY.equals(suffix)) {
				root = text;
			} else if (StringUtils.EMPTY.equals(prefix) && !StringUtils.EMPTY.equals(suffix)) {
				root = text.substring(0, text.length() - suffix.length());
			} else if (!StringUtils.EMPTY.equals(prefix) && StringUtils.EMPTY.equals(suffix)) {
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
