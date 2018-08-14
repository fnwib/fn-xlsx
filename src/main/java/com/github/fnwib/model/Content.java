package com.github.fnwib.model;

import lombok.EqualsAndHashCode;
import lombok.ToString;

@EqualsAndHashCode
@ToString
public class Content {
	private int columnIndex;
	private String value;

	public Content(int columnIndex, String value) {
		this.columnIndex = columnIndex;
		this.value = value;
	}

	public int getColumnIndex() {
		return columnIndex;
	}

	public String getValue() {
		return value;
	}
}