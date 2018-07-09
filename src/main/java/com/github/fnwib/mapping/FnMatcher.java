package com.github.fnwib.mapping;

import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.util.ValueUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FnMatcher {
	private static final Logger log = LoggerFactory.getLogger(FnMatcher.class);
	private final Pattern titlePattern;
	private final String prefix;
	private final String sequence;
	private final String suffix;
	private final String exclude;
	private final Collection<ValueHandler> valueHandlers;

	public FnMatcher(BindParam bindParam, LocalConfig localConfig) {
		this.titlePattern = Pattern.compile(bindParam.getTitle(), Pattern.CASE_INSENSITIVE);
		this.prefix = bindParam.getPrefix();
		this.sequence = bindParam.getTitle();
		this.suffix = bindParam.getSuffix();
		this.exclude = bindParam.getExclude();
		this.valueHandlers = localConfig.getTitleValueHandlers();
	}

	public List<Integer> match(Row row) {
		Map<Integer, String> cells = Maps.newHashMapWithExpectedSize(row.getLastCellNum() + 1);
		for (Cell cell : row) {
			Optional<String> cellValue = ValueUtil.getCellValue(cell, valueHandlers);
			cellValue.ifPresent(v -> cells.put(cell.getColumnIndex(), v));
		}
		return match(cells);
	}

	public List<Integer> match(Map<Integer, String> row) {
		if (StringUtils.isBlank(prefix) && StringUtils.isBlank(sequence) && StringUtils.isBlank(suffix)) {
			return Collections.emptyList();
		}
		List<Integer> bindColumns = new ArrayList<>();
		row.forEach((columnIndex, value) -> {
			Optional<String> root = ValueUtil.substringBetweenIgnoreCase(value, prefix, suffix);
			if (!root.isPresent()) {
				return;
			}
			String mid = StringUtils.trimToEmpty(root.get());
			Matcher titleMatcher = titlePattern.matcher(mid);
			if (titleMatcher.matches()) {
				if (StringUtils.isNotBlank(exclude) && Pattern.matches(exclude, root.get().trim())) {
					return;
				}
				bindColumns.add(columnIndex);
			}
		});
		return bindColumns;
	}

}
