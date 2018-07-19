package com.github.fnwib.mapper.cell;

import com.github.fnwib.util.ValueUtil;
import com.monitorjbl.xlsx.impl.StreamingRow;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public class DefaultHandler implements CellValueHandler {

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
				return ValueUtil.getCellValue(cell);
			case NUMERIC:
				if (row.getClass() == StreamingRow.class) {
					return Optional.of(cell.getStringCellValue());
				}
				return Optional.of(cell.getNumericCellValue() + StringUtils.EMPTY);
			case ERROR:
			case BOOLEAN:
			case FORMULA:
			case _NONE:
				throw ErrorCellType.NOT_SUPPORT.getException(cell);
			default:
				throw ErrorCellType.UNKNOWN_TYPE.getException(cell);
		}

	}
}
