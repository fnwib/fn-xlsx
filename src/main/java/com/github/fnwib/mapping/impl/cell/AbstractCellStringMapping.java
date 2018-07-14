package com.github.fnwib.mapping.impl.cell;

import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public abstract class AbstractCellStringMapping implements CellMapping {

	@Override
	public abstract Optional<String> getValue(int indexColumn, Row row);

}
