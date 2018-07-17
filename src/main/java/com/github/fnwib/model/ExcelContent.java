package com.github.fnwib.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;
import org.apache.poi.ss.usermodel.Cell;

/**
 * EXCEL 内容
 */
@EqualsAndHashCode
@ToString
@Getter
public class ExcelContent {
	private int columnIndex;
	private String value;

	//保留原有cell全部信息
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
