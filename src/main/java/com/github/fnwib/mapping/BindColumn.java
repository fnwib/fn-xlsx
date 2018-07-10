package com.github.fnwib.mapping;

import java.util.Objects;

public class BindColumn {
	/**
	 * cell columnIndex
	 */
	private Integer index;
	/**
	 * cell getStringValue
	 */
	private String text;
	/**
	 * FnMatcher 去掉prefix和suffix 剩下的
	 */
	private String mid;

	public BindColumn(Integer index, String text, String mid) {
		this.index = Objects.requireNonNull(index, "cell columnIndex must be not null");
		this.text = Objects.requireNonNull(text);
		this.mid = Objects.requireNonNull(mid);
	}

	public Integer getIndex() {
		return index;
	}

	public String getText() {
		return text;
	}

	public String getMid() {
		return mid;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BindColumn that = (BindColumn) o;

		if (index != null ? !index.equals(that.index) : that.index != null) return false;
		if (text != null ? !text.equals(that.text) : that.text != null) return false;
		return mid != null ? mid.equals(that.mid) : that.mid == null;
	}

	@Override
	public int hashCode() {
		int result = index != null ? index.hashCode() : 0;
		result = 31 * result + (text != null ? text.hashCode() : 0);
		result = 31 * result + (mid != null ? mid.hashCode() : 0);
		return result;
	}
}
