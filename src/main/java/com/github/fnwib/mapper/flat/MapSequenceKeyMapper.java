package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.title.Sequence;
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
 * MAP key为@BindCoumn.sequence() 实现
 * <p>
 * Map  读value为null时,不会赋值
 * row {1:"1",2:"2",3:"3"} -> map {1:"1",2:"2"}
 */
public class MapSequenceKeyMapper extends AbstractContainerMapper {

	private CellValueHandler handler;

	public MapSequenceKeyMapper(String name, JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(name, columns);
		this.handler = CellValueHandlers.createCellValueHandler(contentType, valueHandlers);
	}

	@Override
	public Optional<Map<Sequence, Object>> getValue(Row row) {
		if (super.columns.isEmpty()) {
			return Optional.of(Collections.emptyMap());
		}
		Map<Sequence, Object> result = Maps.newHashMapWithExpectedSize(super.columns.size());
		for (BindColumn column : super.columns) {
			Optional<?> value = handler.getValue(column.getIndex(), row);
			value.ifPresent(v -> result.put(column.getSequence(), v));
		}
		return Optional.of(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Content> getContents(Object value) {
		Map<Sequence, String> values = value == null ? Collections.emptyMap() : (Map<Sequence, String>) value;
		check(values.size());
		List<Content> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Integer index = column.getIndex();
			Sequence sequence = column.getSequence();
			String val = values.get(sequence);
			contents.add(new Content(index, val));
		}
		return contents;
	}
}
