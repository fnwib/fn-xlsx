package com.github.fnwib.mapper.cell;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

/**
 * 统一的错误输出
 */
public enum ErrorCellType {
	UNKNOWN_TYPE("unknown type"),
	STRING_TO_DATE("不支持文本转日期"),
	NOT_SUPPORT("不支持的类型"),
	CELL_NUMERIC_TO_STRING("类型使用错误 | 两个解决办法 1.修改Excel CellType  2.字段类型不要使用String");

	private String format;

	private static final String errorType = "ErrorType : {[%s]}";
	private static final String cell = " Cell : { 坐标[%s, %s] 值为'%s' 类型是'%s' } ";

	ErrorCellType(String format) {
		this.format = errorType + cell + " message : {" + format + "}";
	}

	public ExcelException getException(Cell cell) {
		if (this == UNKNOWN_TYPE) {
			return get(cell);
		} else {
			return get(cell);
		}
	}

	private ExcelException get(Cell cell) {
		Row row = cell.getRow();
		return new ExcelException(format, this.name(), row.getRowNum() + 1,
				ExcelUtil.num2Column(cell.getColumnIndex() + 1),
				cell.getNumericCellValue(),
				cell.getCellTypeEnum().name());
	}

}