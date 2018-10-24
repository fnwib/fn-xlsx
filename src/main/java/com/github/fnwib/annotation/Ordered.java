package com.github.fnwib.annotation;

public interface Ordered {
	/**
	 * 高优先级
	 */
	int HIGHEST_PRECEDENCE = Integer.MIN_VALUE;
	/**
	 * 低优先级
	 */
	int LOWEST_PRECEDENCE = Integer.MAX_VALUE;

}
