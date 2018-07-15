package com.github.fnwib.mapper.cell;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.util.ExcelUtil;
import com.github.fnwib.util.ValueUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collection;
import java.util.Optional;

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
			case ERROR:
			case FORMULA:
			case BOOLEAN:
			case _NONE:
				String format = String.format("坐标[%s][%s]值为[%s],类型是[%s]",
						row.getRowNum() + 1,
						ExcelUtil.num2Column(cell.getColumnIndex() + 1),
						cell.getStringCellValue(),
						cell.getCellTypeEnum().name());
				throw new ExcelException(format);
			default:
				throw new NotSupportedException(" [" + cell.getStringCellValue() + "] unknown type");
		}

	}

}
