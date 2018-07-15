package com.github.fnwib.mapper.cell;

import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

@FunctionalInterface
public interface CellHandler {

	Optional<?> getValue(int indexColumn, Row row);

}
