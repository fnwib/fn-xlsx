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
public class Content {
	private int columnIndex;
	private String value;

	//保留原有cell全部信息
	private Cell cell;

	public Content(int columnIndex, String value) {
		this.columnIndex = columnIndex;
		this.value = value;
	}

	public Content(Cell cell) {
		this.cell = cell;
	}

	public boolean isCell() {
		return cell != null;
	}
}
