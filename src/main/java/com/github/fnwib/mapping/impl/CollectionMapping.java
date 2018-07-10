package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapping.BindColumn;
import com.github.fnwib.mapping.Mappings;
import com.github.fnwib.mapping.impl.cell.*;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public class CollectionMapping implements BindMapping {

	private AbstractCellStringMapping mapping;
	private List<BindColumn> columns;

	public CollectionMapping(JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		this.mapping = Mappings.createSimpleMapping(contentType,valueHandlers);
		this.columns = columns;
	}

	@Override
	public List<BindColumn> getColumns() {
		return columns;
	}

	@Override
	public Optional<Collection<String>> getValue(Row row) {
		if (columns.isEmpty()) {
			return Optional.empty();
		}
		Collection<String> result = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Optional<String> value = mapping.getValue(column.getIndex(), row);
			result.add(value.orElse(StringUtils.EMPTY));
		}
		return Optional.of(result);
	}

	@Override
	public void setValueToRow(Object value, Row row) {
		if (value == null) {
			return;
		}
		List<Object> objects = (List<Object>) value;
		if (objects.size() > columns.size()) {
			String format = String.format("当前集合数量'%s'大于允许写入数量'%s'", objects.size(), columns.size());
			throw new ExcelException(format);
		}
		int i = 0;
		for (Object object : objects) {
			BindColumn column = columns.get(i);
			mapping.setValueToRow(object, column.getIndex(), row);
			i++;
		}
	}
}
