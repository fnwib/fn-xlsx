package com.github.fnwib.mapper.cell;

import com.github.fnwib.plugin.ValueHandler;
import com.github.fnwib.util.ValueUtil;
import com.monitorjbl.xlsx.impl.StreamingRow;
import lombok.extern.slf4j.Slf4j;
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
				if (row.getClass() == StreamingRow.class) {
					return ValueUtil.getCellValue(cell, valueHandlers);
				}
				throw ErrorCellType.CELL_NUMERIC_TO_STRING.getException(cell);
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
