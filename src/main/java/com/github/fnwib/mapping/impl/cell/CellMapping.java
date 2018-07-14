package com.github.fnwib.mapping.impl.cell;

import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public interface CellMapping {

	Optional<?> getValue(int indexColumn, Row row);

}
