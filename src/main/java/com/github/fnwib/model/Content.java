package com.github.fnwib.model;

import org.apache.poi.ss.usermodel.Cell;

/**
 * EXCEL 内容
 */
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

	public int getColumnIndex() {
		return columnIndex;
	}

	public String getValue() {
		return value;
	}

	public Cell getCell() {
		return cell;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Content content = (Content) o;

		if (columnIndex != content.columnIndex) return false;
		if (value != null ? !value.equals(content.value) : content.value != null) return false;
		return cell != null ? cell.equals(content.cell) : content.cell == null;
	}

	@Override
	public int hashCode() {
		int result = columnIndex;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		result = 31 * result + (cell != null ? cell.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Content{" +
				"columnIndex=" + columnIndex +
				", value='" + value + '\'' +
				", cell=" + cell +
				'}';
	}
}
