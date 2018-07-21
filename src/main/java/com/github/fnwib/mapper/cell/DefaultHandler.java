package com.github.fnwib.mapper.cell;

import com.github.fnwib.plugin.ValueHandler;
import com.github.fnwib.util.ValueUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.NumberToTextConverter;

import java.util.Collection;
import java.util.Optional;

public class DefaultHandler implements CellValueHandler {

	private final Collection<ValueHandler> valueHandlers;

	public DefaultHandler(Collection<ValueHandler> valueHandlers) {
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
			case _NONE:
				return Optional.empty();
			case STRING:
				return ValueUtil.getCellValue(cell, valueHandlers);
			case NUMERIC:
				String text = NumberToTextConverter.toText(cell.getNumericCellValue());
				return Optional.of(text);
			case ERROR:
			case BOOLEAN:
			case FORMULA:
				throw ErrorCellType.NOT_SUPPORT.getException(cell);
			default:
				throw ErrorCellType.UNKNOWN_TYPE.getException(cell);
		}

	}
}
