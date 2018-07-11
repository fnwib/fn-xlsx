package com.github.fnwib.mapping.model;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.BindType;
import com.github.fnwib.annotation.Complex;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.annotation.Ordered;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapping.impl.BindMapping;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

@Builder
@Getter
public class BindProperty {
	/**
	 * 该属性属于哪个类
	 */
	private Class<?> region;

	/**
	 * 字段带泛型类型
	 */
	private JavaType type;

	/**
	 * 当前字段name
	 */
	private String propertyName;
	/**
	 * 获取行号或者cell 取值
	 */
	private Operation operation;
	/**
	 * String 的值处理器列表
	 */
	private Collection<ValueHandler> valueHandlers;

	/**
	 * 匹配参数
	 */
	private Rule rule;

	/**
	 * 同一层级绑定优先级
	 *
	 * @return
	 */
	private int order;

	/**
	 * Y 两层(最多两层)
	 * N 一层
	 *
	 * @return
	 */
	private Complex complex;

	/**
	 * 绑定模式 : 共享  or  独占
	 */
	private BindType bindType;


	@Setter
	private BindMapping bindMapping;
	@Setter
	private Method readMethod;
	@Setter
	private Method writeMethod;
	@Setter
	private List<BindProperty> subBindProperties;
	@Setter
	private List<BindColumn> bindColumns;

	/**
	 * 判断该属性是否属于
	 *
	 * @param bindClass
	 * @return
	 */
	public boolean isRegion(Class<?> bindClass) {
		return region == bindClass;
	}

}
