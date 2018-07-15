package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.Mappings;
import com.github.fnwib.mapper.cell.AbstractCellHandler;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

/**
 * Map key为ExcelHeader.value 实现
 */
public class MapTextKeyMapper extends AbstractContainerMapper {

	private AbstractCellHandler mapping;

	public MapTextKeyMapper(String name, JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(name, columns);
		this.mapping = Mappings.createCellHandler(contentType, valueHandlers);
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
		check(values.size());
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
