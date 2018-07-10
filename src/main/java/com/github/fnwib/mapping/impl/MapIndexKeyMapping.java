package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapping.impl.cell.CellMapping;
import com.github.fnwib.mapping.impl.cell.NumberMapping;
import com.github.fnwib.mapping.impl.cell.SimpleMapping;
import com.github.fnwib.mapping.impl.cell.StringMapping;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;
import java.util.stream.Collectors;

public class MapIndexKeyMapping implements BindMapping {

	private List<CellMapping> cellMappings;

	public MapIndexKeyMapping(JavaType keyType, JavaType contentType, List<Integer> columns, Collection<ValueHandler> valueHandlers) {
		List<CellMapping> bindMappings;
		Class<?> rawClass = contentType.getRawClass();
		if (String.class == rawClass) {
			bindMappings = columns.stream().map(c -> new StringMapping(c, valueHandlers)).collect(Collectors.toList());
		} else if (Number.class.isAssignableFrom(rawClass)) {
			bindMappings = columns.stream().map(NumberMapping::new).collect(Collectors.toList());
		} else {
			bindMappings = columns.stream().map(c -> new SimpleMapping(contentType, c)).collect(Collectors.toList());
		}
		this.cellMappings = bindMappings;
	}

	@Override
	public List<CellMapping> getCellMappings() {
		return cellMappings;
	}

	@Override
	public Optional<Map<Integer, String>> getValue(Row row) {
		if (cellMappings.isEmpty()) {
			return Optional.of(Collections.emptyMap());
		}
		Map<Integer, String> result = Maps.newHashMapWithExpectedSize(cellMappings.size());
		for (CellMapping mapping : cellMappings) {
			Integer column = mapping.getColumn();
			Optional<String> value = mapping.getValue(row);
			value.ifPresent(v -> result.put(column, v));
		}
		return Optional.of(result);
	}

}
