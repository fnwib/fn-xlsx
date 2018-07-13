package com.github.fnwib.write.model;

import lombok.Getter;

/**
 * EXCEL 内容
 */
@Getter
public class ExcelContent {
	private int columnIndex;
	private String value;

	public ExcelContent(int columnIndex, String value) {
		this.columnIndex = columnIndex;
		this.value = value;
	}
}
