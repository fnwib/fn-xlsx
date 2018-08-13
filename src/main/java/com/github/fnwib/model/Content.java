package com.github.fnwib.model;

/**
 * EXCEL 内容
 */
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Content content = (Content) o;

		if (columnIndex != content.columnIndex) return false;
		return value != null ? value.equals(content.value) : content.value == null;
	}

	@Override
	public int hashCode() {
		int result = columnIndex;
		result = 31 * result + (value != null ? value.hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		return "Content{" +
				"columnIndex=" + columnIndex +
				", value='" + value + '\'' +
				'}';
	}
}
