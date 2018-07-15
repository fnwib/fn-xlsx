package com.github.fnwib.mapping.flat;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class LineNumMapping implements FlatMapping {
	private List<BindColumn> columns;

	public LineNumMapping(List<BindColumn> columns) {
		if (columns.size() > 2) {
			throw new SettingException("LineNum匹配到多列");
		}
		this.columns = columns;
	}

	@Override
	public List<BindColumn> getColumns() {
		return columns;
	}

	@Override
	public Optional<String> getValue(Row row) {
		int s = row.getRowNum() + 1;
		return Optional.of(s + StringUtils.EMPTY);
	}

	@Override
	public List<ExcelContent> getContents(Object value) {
		if (columns.isEmpty()) {
			return Collections.emptyList();
		}
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Integer index = column.getIndex();
			String val = Objects.isNull(value) ? null : value.toString();
			contents.add(new ExcelContent(index, val));
		}
		return contents;
	}
}
