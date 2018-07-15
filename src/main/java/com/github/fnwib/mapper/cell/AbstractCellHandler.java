package com.github.fnwib.mapper.cell;

import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public abstract class AbstractCellHandler implements CellHandler {

	@Override
	public abstract Optional<String> getValue(int indexColumn, Row row);

}
