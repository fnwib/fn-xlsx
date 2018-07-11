package com.github.fnwib.mapping.model;

import com.github.fnwib.annotation.BindType;
import com.github.fnwib.annotation.Complex;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.annotation.RWType;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@ToString
@Builder
@Getter
public class SpecialConfig {

	/**
	 * 获取行号或者cell 取值
	 */
	private Operation operation;

	/**
	 * 同一层级绑定优先级
	 */
	private int order;

	/**
	 * Y 两层(最多两层)
	 * N 一层
	 */
	private Complex complex;

	/**
	 * 绑定模式 : 共享  or  独占
	 */
	private BindType bindType;

	/**
	 * 读写配置
	 */
	private RWType rw;
}
