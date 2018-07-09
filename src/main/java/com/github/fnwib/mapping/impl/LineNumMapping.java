package com.github.fnwib.mapping.impl;

import com.github.fnwib.mapping.BindMapping;
import com.github.fnwib.write.CellText;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public class LineNumMapping implements BindMapping {
	@Override
	public Optional<String> getValue(Row row) {
		int s = row.getRowNum() + 1;
		return Optional.of(s + StringUtils.EMPTY);
	}

	@Override
	public Optional<CellText> createCellText(Object value) {
		return Optional.empty();
	}
}
