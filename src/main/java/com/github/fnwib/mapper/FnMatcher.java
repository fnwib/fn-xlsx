package com.github.fnwib.mapper;

import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.mapper.model.MatchConfig;
import com.github.fnwib.model.Header;
import com.github.fnwib.plugin.ValueHandler;
import com.github.fnwib.util.ValueUtil;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

@Slf4j
public class FnMatcher {
	private final MatchConfig matchConfig;
	private final Collection<ValueHandler> valueHandlers;

	public FnMatcher(MatchConfig matchConfig, LocalConfig localConfig) {
		this.matchConfig = matchConfig;
		this.valueHandlers = localConfig.getTitleValueHandlers();
	}

	public List<BindColumn> match(List<Header> headers, Set<Integer> ignoreColumns) {
		Map<Integer, String> cells = Maps.newHashMapWithExpectedSize(headers.size());
		for (Header cell : headers) {
			if (ignoreColumns.contains(cell.getColumnIndex())) {
				continue;
			}
			Optional<String> cellValue = ValueUtil.getStringValue(cell.getValue(), valueHandlers);
			cellValue.ifPresent(v -> cells.put(cell.getColumnIndex(), v));
		}
		return matchConfig.match(cells);
	}


}
