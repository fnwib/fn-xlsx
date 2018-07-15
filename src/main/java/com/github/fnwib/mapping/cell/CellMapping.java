package com.github.fnwib.mapping.cell;

import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

@FunctionalInterface
public interface CellMapping {

	Optional<?> getValue(int indexColumn, Row row);

}
