package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.convert.impl.BeanConverter;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.BindColumn;
import com.github.fnwib.mapping.impl.cell.CellMapping;
import com.github.fnwib.mapping.impl.cell.NumberMapping;
import com.github.fnwib.mapping.impl.cell.SimpleMapping;
import com.github.fnwib.mapping.impl.cell.StringMapping;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;
import java.util.stream.Collectors;

public class MapMapping implements BindMapping {

	private Map<String, CellMapping> cellMappings;

	public MapMapping(JavaType keyType, JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		Map<String, CellMapping> map = Maps.newHashMapWithExpectedSize(columns.size());
		for (BindColumn column : columns) {
			String key;
			if (Integer.class == keyType.getRawClass()) {
				key = column.getIndex().toString();
			} else if (Sequence.class == keyType.getRawClass()) {
				key = column.getMid();
			} else if (String.class == keyType.getRawClass()) {
				key = column.getText();
			} else {
				String format = String.format("Map类型的key只支持 %s(cell index) | %s (cell name ) | %s (cell sequence)", Integer.class, String.class, Sequence.class);
				throw new SettingException(format);
			}
			CellMapping cellMapping;
			Class<?> rawClass = contentType.getRawClass();
			if (String.class == rawClass) {
				cellMapping = new StringMapping(column.getIndex(), valueHandlers);
			} else if (Number.class.isAssignableFrom(rawClass)) {
				cellMapping = new NumberMapping(column.getIndex());
			} else {
				cellMapping = new SimpleMapping(contentType, column.getIndex());
			}
			map.put(key, cellMapping);
		}
		this.cellMappings = map;
	}

	@Override
	public List<CellMapping> getCellMappings() {
		List<CellMapping> ss = Lists.newArrayListWithCapacity(cellMappings.size());
		cellMappings.forEach((_ig, mapping) -> ss.add(mapping));
		return ss;
	}

	@Override
	public Optional<Map<String, String>> getValue(Row row) {
		if (cellMappings.isEmpty()) {
			return Optional.of(Collections.emptyMap());
		}
		Map<String, String> result = Maps.newHashMapWithExpectedSize(cellMappings.size());
		cellMappings.forEach((key, mapping) -> mapping.getValue(row).ifPresent(v -> result.put(key, v)));
		return Optional.of(result);
	}

}
