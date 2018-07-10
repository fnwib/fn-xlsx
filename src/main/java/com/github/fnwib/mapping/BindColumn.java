package com.github.fnwib.mapping;

import com.github.fnwib.databing.title.Sequence;

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
	private Sequence sequence;

	public BindColumn(Integer index, String text, String mid) {
		this.index = Objects.requireNonNull(index, "cell columnIndex must be not null");
		this.text = Objects.requireNonNull(text);
		this.sequence = new Sequence(Objects.requireNonNull(mid));
	}

	public Integer getIndex() {
		return index;
	}

	public String getText() {
		return text;
	}

	public Sequence getSequence() {
		return sequence;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		BindColumn column = (BindColumn) o;

		if (index != null ? !index.equals(column.index) : column.index != null) return false;
		if (text != null ? !text.equals(column.text) : column.text != null) return false;
		return sequence != null ? sequence.equals(column.sequence) : column.sequence == null;
	}

	@Override
	public int hashCode() {
		int result = index != null ? index.hashCode() : 0;
		result = 31 * result + (text != null ? text.hashCode() : 0);
		result = 31 * result + (sequence != null ? sequence.hashCode() : 0);
		return result;
	}
}
