package com.github.fnwib.mapping.impl;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.BindColumn;
import com.github.fnwib.mapping.impl.cell.CellMapping;
import com.github.fnwib.mapping.impl.cell.NumberMapping;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LineNumMapping implements BindMapping {
	private List<BindColumn> columns;
	private NumberMapping mapping;

	public LineNumMapping(List<BindColumn> columns) {
		if (columns.size() > 2) {
			throw new SettingException();
		}
		this.columns = columns;
		this.mapping = new NumberMapping();
	}

	@Override
	public List<BindColumn> getColumns() {
		return columns;
	}

	@Override
	public Optional<String> getValue(Row row) {
		int s = row.getRowNum() + 1;
		return Optional.of(s + StringUtils.EMPTY);
	}

	@Override
	public void setValueToRow(Object value, Row row) {
		if (columns.isEmpty()) {
			return;
		}
		BindColumn column = columns.get(0);
		mapping.setValueToRow(value, column.getIndex(), row);
	}

}
