package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapper.cell.CellValueHandler;
import com.github.fnwib.mapper.cell.CellValueHandlers;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

/**
 * MAP key为@AutoMapping.value() 实现
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
	public List<ExcelContent> getContents(Object value) {
		Map<Sequence, String> values = value == null ? Collections.emptyMap() : (Map<Sequence, String>) value;
		check(values.size());
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Integer index = column.getIndex();
			Sequence sequence = column.getSequence();
			String val = values.get(sequence);
			contents.add(new ExcelContent(index, val));
		}
		return contents;
	}
}
