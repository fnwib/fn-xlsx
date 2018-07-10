package com.github.fnwib.mapping.impl.cell;

import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public interface CellMapping {

	Integer getColumn();

	Optional<?> getValue(Row row);

	void setValueToRow(Object value, Row row);

}
