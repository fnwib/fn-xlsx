package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.impl.cell.AbstractCellStringMapping;
import com.github.fnwib.mapping.Mappings;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;
import java.util.stream.Collectors;

public class MapTextKeyMapping extends AbstractMapMapping {

	private AbstractCellStringMapping mapping;

	private Map<String, Integer> map;

	public MapTextKeyMapping(JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(columns);
		map = columns.stream().collect(Collectors.toMap(BindColumn::getText, BindColumn::getIndex));
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
	public List<ExcelContent> getContents(Object value) {
		Map<String, String> values = value == null ? Collections.emptyMap() : (Map<String, String>) value;
		if (values.size() > columns.size()) {
			throw new ExcelException("当前集合数量'%s'大于允许写入数量'%s'", values.size(), columns.size());
		}
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Integer index = column.getIndex();
			String text = column.getText();
			String val = values.get(text);
			contents.add(new ExcelContent(index, val));
		}
		return contents;
	}
}
