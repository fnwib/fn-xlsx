package com.github.fnwib.mapper.cell;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.util.ValueUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collection;
import java.util.Optional;

@Slf4j
public class StringHandler implements CellValueHandler {

	private final Collection<ValueHandler> valueHandlers;

	public StringHandler(Collection<ValueHandler> valueHandlers) {
		this.valueHandlers = valueHandlers;
	}

	@Override
	public Optional<String> getValue(int indexColumn, Row row) {
		Cell cell = row.getCell(indexColumn);
		if (cell == null) {
			return Optional.empty();
		}
		switch (cell.getCellTypeEnum()) {
			case BLANK:
				return Optional.empty();
			case STRING:
				return ValueUtil.getCellValue(cell, valueHandlers);
			case NUMERIC:
//				throw ErrorCellType.CELL_NUMERIC_TO_STRING.getException(cell);
				return Optional.of(cell.getNumericCellValue() + StringUtils.EMPTY);
			case ERROR:
			case FORMULA:
			case BOOLEAN:
			case _NONE:
				throw ErrorCellType.NOT_SUPPORT.getException(cell);
			default:
				throw ErrorCellType.UNKNOWN_TYPE.getException(cell);
		}

	}

}
