package com.github.fnwib.model;

import lombok.Getter;

import java.util.List;

/**
 * EXCEL 内容
 */
@Getter
public class RowExcelContent {

	private List<ExcelContent> row;

	public RowExcelContent(List<ExcelContent> row) {
		this.row = row;
	}
}
