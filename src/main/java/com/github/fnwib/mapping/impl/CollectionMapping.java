package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapping.impl.cell.CellMapping;
import com.github.fnwib.mapping.impl.cell.NumberMapping;
import com.github.fnwib.mapping.impl.cell.SimpleMapping;
import com.github.fnwib.mapping.impl.cell.StringMapping;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CollectionMapping implements BindMapping {

	private List<CellMapping> cellMappings;

	public CollectionMapping(JavaType contentType, List<Integer> columns, Collection<ValueHandler> valueHandlers) {
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
	public Optional<Collection<String>> getValue(Row row) {
		if (cellMappings.isEmpty()) {
			return Optional.empty();
		}
		Collection<String> result = Lists.newArrayListWithCapacity(cellMappings.size());
		for (CellMapping mapping : cellMappings) {
			Optional<String> value = mapping.getValue(row);
			result.add(value.orElse(StringUtils.EMPTY));
		}
		return Optional.of(result);
	}

}
