package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.*;

public class WriteHelper {

	public static void setColumnWidthIfGtZero(Sheet sheet, int cellNum, int width) {
		if (width <= 0) {
			return;
		}
		sheet.setColumnWidth(cellNum, width);
	}

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
		Cell cell = getOrCreateCell(row, cellNum);
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}

	public static Cell setCellValue(Sheet sheet, int rowNum, Cell fromCell) {
		Row row = getOrCreateRow(sheet, rowNum);
		Cell cell = getOrCreateCell(row, fromCell.getColumnIndex());
		switch (fromCell.getCellTypeEnum()) {
			case _NONE:
			case BLANK:
				break;
			case ERROR:
				cell.setCellType(CellType.ERROR);
				cell.setCellErrorValue(fromCell.getErrorCellValue());
				break;
			case STRING:
				cell.setCellType(CellType.STRING);
				cell.setCellValue(fromCell.getStringCellValue());
				break;
			case BOOLEAN:
				cell.setCellType(CellType.BOOLEAN);
				cell.setCellValue(fromCell.getBooleanCellValue());
				break;
			case FORMULA:
				cell.setCellType(CellType.FORMULA);
				cell.setCellFormula(fromCell.getCellFormula());
				break;
			case NUMERIC:
				cell.setCellType(CellType.NUMERIC);
				cell.setCellValue(fromCell.getNumericCellValue());
				break;
			default:
				break;
		}
		return cell;
	}

	public static Row getOrCreateRow(Sheet sheet, int rowNum) {
		Row row = sheet.getRow(rowNum);
		return row == null ? sheet.createRow(rowNum) : row;
	}

	public static void setHeightIfGtZero(Row row, short height) {
		if (height <= 0) {
			return;
		}
		row.setHeight(height);
	}

	public static Cell getOrCreateCell(Row row, int cellNum) {
		Cell cell = row.getCell(cellNum);
		return cell == null ? row.createCell(cellNum) : cell;
	}

	public static void setCellValue(Cell cell, String value, CellStyle cellStyle) {
		cell.setCellValue(value);
		cell.setCellStyle(cellStyle);
	}
}
