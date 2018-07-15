package com.github.fnwib.mapper.cell;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.util.ExcelUtil;
import com.github.fnwib.util.ValueUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public class SimpleHandler extends AbstractCellValueHandler {

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
				return Optional.of(cell.getNumericCellValue() + StringUtils.EMPTY);
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
