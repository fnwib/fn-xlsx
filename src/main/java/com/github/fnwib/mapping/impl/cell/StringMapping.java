package com.github.fnwib.mapping.impl.cell;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.util.ExcelUtil;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collection;
import java.util.Optional;

public class StringMapping extends CellStringMapping {

	private final Collection<ValueHandler> valueHandlers;

	public StringMapping(Integer bindColumn, Collection<ValueHandler> valueHandlers) {
		super(bindColumn);
		this.valueHandlers = valueHandlers;
	}

	@Override
	public Optional<String> getValue(Row row) {
		Cell cell = row.getCell(bindColumn);
		if (cell == null) {
			return Optional.empty();
		}
		switch (cell.getCellTypeEnum()) {
			case BLANK:
				return Optional.empty();
			case NUMERIC:
			case STRING:
				return ValueUtil.getCellValue(cell, valueHandlers);
			case ERROR:
			case BOOLEAN:
			case FORMULA:
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
