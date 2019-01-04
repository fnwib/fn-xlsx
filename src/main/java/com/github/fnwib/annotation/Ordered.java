package com.github.fnwib.annotation;

public final class Ordered {

	private Ordered() {
	}

	/**
	 * 高优先级
	 */
	public static final int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
	/**
	 * 低优先级
	 */
	public static final int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

}
