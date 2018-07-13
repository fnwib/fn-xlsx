package com.github.fnwib.mapping.impl.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public abstract class AbstractCellStringMapping implements CellMapping {

	@Override
	public abstract Optional<String> getValue(int indexColumn, Row row);


	@Override
	public void setValueToRow(Object value, int indexColumn, Row row) {
		Cell cell = row.createCell(indexColumn);
		if (value == null) {
			return;
		}
		cell.setCellType(CellType.STRING);
		cell.setCellValue(value.toString());
	}

}
