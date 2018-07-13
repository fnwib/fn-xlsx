package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

public class WriteHelper {

	/**
	 * 指定位置赋值
	 *
	 * @param rowNum
	 * @param cellNum
	 * @param value
	 * @param cellStyle
	 */
	public static void setValue(Sheet sheet, int rowNum, int cellNum, String value, CellStyle cellStyle) {
		Row row = getOrCreateRow(sheet, rowNum);
		org.apache.poi.ss.usermodel.Cell cell = getOrCreateCell(row, cellNum);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}

	private static Row getOrCreateRow(Sheet sheet, int rowNum) {
		Row row = sheet.getRow(rowNum);
		return row == null ? sheet.createRow(rowNum) : row;
	}

	private static Cell getOrCreateCell(Row row, int cellNum) {
		Cell cell = row.getCell(cellNum);
		return cell == null ? row.createCell(cellNum) : cell;
	}

}
