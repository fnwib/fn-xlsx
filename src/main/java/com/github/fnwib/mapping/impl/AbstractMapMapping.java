package com.github.fnwib.mapping.impl;

import com.github.fnwib.mapping.BindColumn;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;

public abstract class AbstractMapMapping implements BindMapping {

	List<BindColumn> columns;

	public AbstractMapMapping(List<BindColumn> columns) {
		this.columns = columns;
	}

	@Override
	public List<BindColumn> getColumns() {
		return columns;
	}

	@Override
	abstract public Optional<?> getValue(Row row);

	@Override
	abstract public void setValueToRow(Object value, Row row);
}
