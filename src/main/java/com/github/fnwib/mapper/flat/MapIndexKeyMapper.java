package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapper.Mappings;
import com.github.fnwib.mapper.cell.AbstractCellValueHandler;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

/**
 * MAP key为columnIndex 实现
 */
public class MapIndexKeyMapper extends AbstractContainerMapper {

	private AbstractCellValueHandler handler;

	public MapIndexKeyMapper(String name, JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(name, columns);
		this.handler = Mappings.createCellValueHandler(contentType, valueHandlers);
	}

	@Override
	public Optional<Map<Integer, String>> getValue(Row row) {
		if (super.columns.isEmpty()) {
			return Optional.of(Collections.emptyMap());
		}
		Map<Integer, String> result = Maps.newHashMapWithExpectedSize(super.columns.size());
		for (BindColumn column : super.columns) {
			Integer index = column.getIndex();
			Optional<String> value = handler.getValue(index, row);
			value.ifPresent(v -> result.put(index, v));
		}
		return Optional.of(result);
	}

	@Override
	public List<ExcelContent> getContents(Object value) {
		Map<Integer, String> values = value == null ? Collections.emptyMap() : (Map<Integer, String>) value;
		check(values.size());
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
