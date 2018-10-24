package com.github.fnwib.jackson;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Sequence {

	private String value;

	public Sequence(String value) {
		this.value = value;
	}

	public Sequence(int value) {
		this.value = value + "";
	}

	public Integer asInt() {
		return Integer.parseInt(value);
	}

	/**
	 * @return
	 * @see CellSequenceDeserializer
	 */
	@Override
	public String toString() {
		return value;
	}

}
