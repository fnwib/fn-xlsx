package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.impl.cell.CellMapping;
import com.github.fnwib.mapping.impl.cell.NumberMapping;
import com.github.fnwib.mapping.impl.cell.SimpleMapping;
import com.github.fnwib.mapping.impl.cell.StringMapping;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

public class PrimitiveMapping implements BindMapping {


	private CellMapping cellMapping;
	private BindColumn column;

	public PrimitiveMapping(JavaType javaType, BindColumn column, Collection<ValueHandler> valueHandlers) {
		Class<?> rawClass = javaType.getRawClass();
		if (String.class == rawClass) {
			cellMapping = new StringMapping(valueHandlers);
		} else if (Number.class.isAssignableFrom(rawClass)) {
			cellMapping = new NumberMapping();
		} else {
			cellMapping = new SimpleMapping(javaType);
		}
		this.column = column;
	}

	@Override
	public List<BindColumn> getColumns() {
		return Lists.newArrayList(column);
	}

	@Override
	public Optional<?> getValue(Row row) {
		return cellMapping.getValue(column.getIndex(), row);
	}

	@Override
	public List<ExcelContent> getContents(Object value) {
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(1);
		Integer index = column.getIndex();
		String val = Objects.isNull(value) ? null : value.toString();
		contents.add(new ExcelContent(index, val));
		return contents;
	}
}
