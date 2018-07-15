package com.github.fnwib.mapper.cell;

import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public abstract class AbstractCellValueHandler implements CellValueHandler {

	@Override
	public abstract Optional<String> getValue(int indexColumn, Row row);

}
