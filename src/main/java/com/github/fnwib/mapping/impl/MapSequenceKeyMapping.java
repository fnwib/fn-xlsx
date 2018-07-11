package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.impl.cell.AbstractCellStringMapping;
import com.github.fnwib.mapping.Mappings;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;
import java.util.stream.Collectors;

public class MapSequenceKeyMapping extends AbstractMapMapping {

	private AbstractCellStringMapping mapping;

	private Map<Sequence, Integer> map;

	public MapSequenceKeyMapping(JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(columns);
		map = columns.stream().collect(Collectors.toMap(BindColumn::getSequence, BindColumn::getIndex));
		this.mapping = Mappings.createSimpleMapping(contentType, valueHandlers);
	}

	@Override
	public Optional<Map<Sequence, String>> getValue(Row row) {
		if (super.columns.isEmpty()) {
			return Optional.of(Collections.emptyMap());
		}
		Map<Sequence, String> result = Maps.newHashMapWithExpectedSize(super.columns.size());
		for (BindColumn column : super.columns) {
			Optional<String> value = mapping.getValue(column.getIndex(), row);
			value.ifPresent(v -> result.put(column.getSequence(), v));
		}
		return Optional.of(result);
	}

	@Override
	public void setValueToRow(Object value, Row row) {
		if (value == null) {
			return;
		}
		Map<Sequence, String> values = (Map<Sequence, String>) value;
		if (values.size() > columns.size()) {
			String format = String.format("当前集合数量'%s'大于允许写入数量'%s'", values.size(), columns.size());
			throw new ExcelException(format);
		}
		values.forEach((sequence, val) -> {
			Integer index = map.get(sequence);
			mapping.setValueToRow(val, index, row);
		});
	}
}
