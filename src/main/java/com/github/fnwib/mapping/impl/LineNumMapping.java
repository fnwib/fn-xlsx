package com.github.fnwib.mapping.impl;

import com.github.fnwib.mapping.impl.cell.CellMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LineNumMapping implements BindMapping {

	@Override
	public List<CellMapping> getCellMappings() {
		return Collections.emptyList();
	}

	@Override
	public Optional<String> getValue(Row row) {
		int s = row.getRowNum() + 1;
		return Optional.of(s + StringUtils.EMPTY);
	}

}
