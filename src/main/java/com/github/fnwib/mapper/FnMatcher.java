package com.github.fnwib.mapper;

import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.mapper.model.MatchConfig;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.model.ExcelHeader;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class FnMatcher {
	private final Pattern titlePattern;
	private final String prefix;
	private final String sequence;
	private final String suffix;
	private final String exclude;
	private final Collection<ValueHandler> valueHandlers;

	public FnMatcher(MatchConfig rule, LocalConfig localConfig) {
		this.titlePattern = Pattern.compile(rule.getTitle(), Pattern.CASE_INSENSITIVE);
		this.prefix = rule.getPrefix();
		this.sequence = rule.getTitle();
		this.suffix = rule.getSuffix();
		this.exclude = rule.getExclude();
		this.valueHandlers = localConfig.getTitleValueHandlers();
	}

	public List<BindColumn> match(List<ExcelHeader> headers, Set<Integer> ignoreColumns) {
		Map<Integer, String> cells = Maps.newHashMapWithExpectedSize(headers.size());
		for (ExcelHeader cell : headers) {
			if (ignoreColumns.contains(cell.getColumnIndex())) {
				continue;
			}
			Optional<String> cellValue = ValueUtil.getStringValue(cell.getValue(), valueHandlers);
			cellValue.ifPresent(v -> cells.put(cell.getColumnIndex(), v));
		}
		return match(cells);
	}

	public List<BindColumn> match(Map<Integer, String> row) {
		if (StringUtils.isBlank(prefix) && StringUtils.isBlank(sequence) && StringUtils.isBlank(suffix)) {
			return Collections.emptyList();
		}
		List<BindColumn> bindColumns = new ArrayList<>();
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
				bindColumns.add(new BindColumn(columnIndex, value, mid));
			}
		});
		return bindColumns;
	}

}
