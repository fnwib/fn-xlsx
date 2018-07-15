package com.github.fnwib.mapping.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.cell.AbstractCellStringMapping;
import com.github.fnwib.mapping.Mappings;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

public class MapSequenceKeyMapping extends AbstractMapMapping {

	private AbstractCellStringMapping mapping;

	public MapSequenceKeyMapping(JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(columns);
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
		Map<Sequence, String> values = value == null ? Collections.emptyMap() : (Map<Sequence, String>) value;
		if (values.size() > columns.size()) {
			throw new ExcelException("当前集合数量'%s'大于允许写入数量'%s'", values.size(), columns.size());
		}
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
