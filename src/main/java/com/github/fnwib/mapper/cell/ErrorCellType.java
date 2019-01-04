package com.github.fnwib.mapper.cell;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.util.ExcelUtil;
import com.monitorjbl.xlsx.exceptions.NotSupportedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;

import java.util.Objects;

/**
 * 统一的错误输出
 */
public enum ErrorCellType {
	/**
	 * unknown type
	 */
	UNKNOWN_TYPE("unknown type"),
	/**
	 * 不支持文本转日期
	 */
	STRING_TO_DATE("不支持文本转日期"),
	/**
	 * 不支持的类型
	 */
	NOT_SUPPORT("不支持的类型"),
	/**
	 * 错误的日期
	 */
	WRONG_DATE("错误的日期");

	private String format;

	private static final String ERROR_TYPE = "ErrorType : {[%s]}";
	private static final String CELL = " Cell : { 坐标XY[%s%s] 值为'%s' 类型是'%s' } ";

	ErrorCellType(String format) {
		this.format = ERROR_TYPE + CELL + " message : {" + format + "}";
	}

	public ExcelException getException(Cell cell) {
		int r = cell.getRowIndex() + 1;
		int c = cell.getColumnIndex() + 1;
		CellType typeEnum = cell.getCellType();
		Object val = null;
		try {
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
					break;
			}
			return new ExcelException(format, this.name(),
					ExcelUtil.num2Column(c), r,
					Objects.isNull(val) ? "" : val.toString(),
					typeEnum.name());
		} catch (NotSupportedException e) {
			return new ExcelException(format, this.name(),
					ExcelUtil.num2Column(c), r,
					"",
					"StreamingCell not support " + typeEnum.name());
		} catch (RuntimeException e) {
			return new ExcelException(format, this.name(),
					ExcelUtil.num2Column(c), r,
					"",
					"Cell not support " + typeEnum.name());
		}
	}

}