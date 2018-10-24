package com.github.fnwib.jackson;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
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

}
