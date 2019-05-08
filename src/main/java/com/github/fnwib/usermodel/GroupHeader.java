package com.github.fnwib.usermodel;

import java.awt.*;
import java.util.Objects;

/**
 * @author fengweibin
 * @date 2019-03-13
 */
public class GroupHeader {
	private Color color;
	private Class<?> type;

	public GroupHeader(Class<?> type) {
		this(type, null);
	}

	public GroupHeader(Class<?> type, Color color) {
		this.type = Objects.requireNonNull(type);
		this.color = color;
	}

	public Color getColor() {
		return color;
	}

	public Class<?> getType() {
		return type;
	}

	@Override
	public String toString() {
		return "GroupHeader{" +
				"color=" + color +
				", type=" + type +
				'}';
	}
}
