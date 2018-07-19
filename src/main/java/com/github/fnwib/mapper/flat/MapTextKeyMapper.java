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
 * MAP key为@BindCoumn.text 实现
 * <p>
 * Map  读value为null时,不会赋值
 * row {"k1":"1","k2":"2","k3":"3"} -> map {"k1":"1","k2":"2"}
 */
public class MapTextKeyMapper extends AbstractContainerMapper {

	private CellValueHandler handler;

	public MapTextKeyMapper(String name, JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(name, columns);
		this.handler = CellValueHandlers.createCellValueHandler(contentType, valueHandlers);
	}

	@Override
	public Optional<Map<String, Object>> getValue(Row row) {
		if (super.columns.isEmpty()) {
			return Optional.of(Collections.emptyMap());
		}
		Map<String, Object> result = Maps.newHashMapWithExpectedSize(super.columns.size());
		for (BindColumn column : super.columns) {
			Optional<?> value = handler.getValue(column.getIndex(), row);
			value.ifPresent(v -> result.put(column.getText(), v));
		}
		return Optional.of(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Content> getContents(Object value) {
		Map<String, String> values = value == null ? Collections.emptyMap() : (Map<String, String>) value;
		check(values.size());
		List<Content> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Integer index = column.getIndex();
			String text = column.getText();
			String val = values.get(text);
			contents.add(new Content(index, val));
		}
		return contents;
	}
}
