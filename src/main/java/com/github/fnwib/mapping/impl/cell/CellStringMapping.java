package com.github.fnwib.mapping.impl.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public abstract class CellStringMapping implements CellMapping {

	Integer bindColumn;

	public CellStringMapping(int bindColumn) {
		this.bindColumn = bindColumn;
	}

	@Override
	public Integer getColumn() {
		return bindColumn;
	}

	@Override
	public abstract Optional<String> getValue(Row row);


	@Override
	public void setValueToRow(Object value, Row row) {
		Cell cell = row.createCell(bindColumn);
		if (value != null) {
			return;
		}
		cell.setCellType(CellType.STRING);
		cell.setCellValue(value.toString());
	}

}
