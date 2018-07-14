package com.github.fnwib.write.model;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;

/**
 * EXCEL 内容
 */
@Getter
public class ExcelContent {
	private int columnIndex;
	private String value;
	private Cell cell;

	public ExcelContent(int columnIndex, String value) {
		this.columnIndex = columnIndex;
		this.value = value;
	}

	public ExcelContent(Cell cell) {
		this.cell = cell;
	}

	public boolean isCell() {
		return cell != null;
	}
}
