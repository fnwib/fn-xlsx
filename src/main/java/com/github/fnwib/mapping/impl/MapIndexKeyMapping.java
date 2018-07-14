package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.Mappings;
import com.github.fnwib.mapping.impl.cell.*;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
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
	public List<ExcelContent> getContents(Object value) {
		Map<Integer, String> values = value == null ? Collections.emptyMap() : (Map<Integer, String>) value;
		if (values.size() > columns.size()) {
			throw new ExcelException("当前集合数量'%s'大于允许写入数量'%s'", values.size(), columns.size());
		}
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Integer index = column.getIndex();
			String val = values.get(index);
			ExcelContent excelContent = new ExcelContent(index, val);
			contents.add(excelContent);
		}
		return contents;
	}
}
