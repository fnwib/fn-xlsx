package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapper.cell.CellValueHandler;
import com.github.fnwib.mapper.cell.CellValueHandlers;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.model.Content;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

/**
 * MAP key为columnIndex 实现
 * <p>
 * Map  读value为null时,不会赋值
 * row {1:"1",2:"2",3:"3"} -> map {1:"1",2:"2"}
 * <p>
 */
public class MapIndexKeyMapper extends AbstractContainerMapper {

	private CellValueHandler handler;

	public MapIndexKeyMapper(String name, JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(name, columns);
		this.handler = CellValueHandlers.createCellValueHandler(contentType, valueHandlers);
	}

	@Override
	public Optional<Map<Integer, Object>> getValue(Row row) {
		if (super.columns.isEmpty()) {
			return Optional.of(Collections.emptyMap());
		}
		Map<Integer, Object> result = Maps.newHashMapWithExpectedSize(super.columns.size());
		for (BindColumn column : super.columns) {
			Integer index = column.getIndex();
			Optional<?> value = handler.getValue(index, row);
			value.ifPresent(v -> result.put(column.getIndex(), v));
		}
		return Optional.of(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Content> getContents(Object value) {
		Map<Integer, String> values = value == null ? Collections.emptyMap() : (Map<Integer, String>) value;
		check(values.size());
		List<Content> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Integer index = column.getIndex();
			String val = values.get(index);
			Content content = new Content(index, val);
			contents.add(content);
		}
		return contents;
	}
}
