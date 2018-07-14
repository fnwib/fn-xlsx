package com.github.fnwib.mapping.model;

import com.github.fnwib.databing.title.Sequence;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@ToString
@Getter
@EqualsAndHashCode
public class BindColumn {
	/**
	 * cell columnIndex
	 */
	private int index;
	/**
	 * cell getStringValue
	 */
	private String text;
	/**
	 * FnMatcher 去掉prefix和suffix 剩下的
	 */
	private Sequence sequence;

	public BindColumn(int index, String text, String mid) {
		this.index = index;
		this.text = Objects.requireNonNull(text);
		this.sequence = new Sequence(Objects.requireNonNull(mid));
	}
}
