package com.github.fnwib.mapper.model;

import com.github.fnwib.annotation.ComplexEnum;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * AutoMapping注解的特性参数
 */
@ToString
@Builder
@Getter
public class FeatureConfig {

	/**
	 * 同一层级绑定优先级
	 */
	private int order;

	/**
	 * Y 两层(最多两层)
	 * N 一层
	 */
	private ComplexEnum complex;

	/**
	 * 绑定模式 : true 共享  or  false  独占
	 */
	private boolean shared;

	/**
	 * 只读
	 */
	private boolean readonly;
}
