package com.github.fnwib.mapper.cell;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public class RawCellMapping implements CellMapping {

	@Override
	public Optional<Cell> getValue(int indexColumn, Row row) {
		return Optional.of(row.getCell(indexColumn));
	}

}
