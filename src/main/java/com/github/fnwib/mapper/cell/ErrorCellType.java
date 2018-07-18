package com.github.fnwib.mapper.cell;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.util.ExcelUtil;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Objects;

/**
 * 统一的错误输出
 */
public enum ErrorCellType {
	UNKNOWN_TYPE("unknown type"),
	STRING_TO_DATE("不支持文本转日期"),
	NOT_SUPPORT("不支持的类型"),
	WRONG_DATE("错误的日期"),
	CELL_NUMERIC_TO_STRING("类型使用错误 | 两个解决办法 1.将Excel单元格样式改为文本  2.字段类型不要使用String");

	private String format;

	private static final String errorType = "ErrorType : {[%s]}";
	private static final String cell = " Cell : { 坐标XY[%s%s] 值为'%s' 类型是'%s' } ";

	ErrorCellType(String format) {
		this.format = errorType + cell + " message : {" + format + "}";
	}

	public ExcelException getException(Cell cell) {
		int r = cell.getRowIndex() + 1;
		int c = cell.getColumnIndex() + 1;
		CellType typeEnum = cell.getCellTypeEnum();
		Object val = null;
		switch (typeEnum) {
			case _NONE:
			case BLANK:
				break;
			case ERROR:
				val = cell.getErrorCellValue();
				break;
			case STRING:
				val = cell.getStringCellValue();
				break;
			case BOOLEAN:
				val = cell.getBooleanCellValue();
				break;
			case FORMULA:
				val = cell.getCellFormula();
				break;
			case NUMERIC:
				val = cell.getNumericCellValue();
				break;
			default:
				val = null;
				break;
		}
		return new ExcelException(format, this.name(),
				ExcelUtil.num2Column(c), r,
				Objects.isNull(val) ? "" : val.toString(),
				typeEnum.name());
	}

}