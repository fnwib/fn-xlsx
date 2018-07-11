package com.github.fnwib.mapping.model;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.BindType;
import com.github.fnwib.annotation.Complex;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapping.impl.BindMapping;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * 规则
 */
@Builder
@Getter
public class BindProperty {

	/**
	 * 字段带泛型类型
	 */
	@Getter
	private JavaType type;

	/**
	 * 当前字段name
	 */
	@Getter
	private String propertyName;

	/**
	 * String 的值处理器列表
	 */
	@Getter
	private Collection<ValueHandler> valueHandlers;

	/**
	 * 匹配配置
	 */
	@Getter
	private MatchConfig matchConfig;

	/**
	 * 特殊配置
	 */
	private SpecialConfig specialConfig;


	/**
	 * 一个规则绑定一个处理器
	 */
	@Setter
	private BindMapping bindMapping;
	@Setter
	private Method readMethod;
	@Setter
	private Method writeMethod;
	/**
	 * 如果是complex == Complex.Y
	 * 则此处不为空
	 */
	@Setter
	private List<BindProperty> subBindProperties;
	/**
	 * 规则绑定的列
	 */
	@Setter
	private List<BindColumn> bindColumns;


	public int getOrder() {
		return specialConfig.getOrder();
	}

	public boolean isComplexY() {
		return specialConfig.getComplex() == Complex.Y;
	}

	public boolean isExclusive() {
		return specialConfig.getBindType() == BindType.Exclusive;
	}

	public boolean isLineNum() {
		return specialConfig.getOperation() == Operation.LINE_NUM;
	}
}
