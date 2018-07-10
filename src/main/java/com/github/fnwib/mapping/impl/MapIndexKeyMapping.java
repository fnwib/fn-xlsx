package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapping.BindColumn;
import com.github.fnwib.mapping.Mappings;
import com.github.fnwib.mapping.impl.cell.*;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

public class MapIndexKeyMapping extends AbstractMapMapping {

	private AbstractCellStringMapping mapping;

	public MapIndexKeyMapping(JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(columns);
		this.mapping = Mappings.createSimpleMapping(contentType, valueHandlers);
	}

	@Override
	public Optional<Map<Integer, String>> getValue(Row row) {
		if (super.columns.isEmpty()) {
			return Optional.of(Collections.emptyMap());
		}
		Map<Integer, String> result = Maps.newHashMapWithExpectedSize(super.columns.size());
		for (BindColumn column : super.columns) {
			Integer index = column.getIndex();
			Optional<String> value = mapping.getValue(index, row);
			value.ifPresent(v -> result.put(index, v));
		}
		return Optional.of(result);
	}

	@Override
	public void setValueToRow(Object value, Row row) {
		if (value == null) {
			return;
		}
		Map<Integer, String> values = (Map<Integer, String>) value;
		if (values.size() > columns.size()) {
			String format = String.format("当前集合数量'%s'大于允许写入数量'%s'", values.size(), columns.size());
			throw new ExcelException(format);
		}
		values.forEach((index, val) -> mapping.setValueToRow(val, index, row));
	}
}
