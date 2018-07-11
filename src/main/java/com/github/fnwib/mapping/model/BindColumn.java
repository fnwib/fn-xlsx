package com.github.fnwib.mapping.model;

import com.github.fnwib.databing.title.Sequence;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.Objects;

@Getter
@EqualsAndHashCode
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
}
