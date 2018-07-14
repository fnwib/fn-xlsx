package com.github.fnwib.mapping.model;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.BindType;
import com.github.fnwib.annotation.ComplexEnum;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapping.impl.BindMapping;
import com.github.fnwib.reflect.Property;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;

/**
 * 规则
 */
@Builder
public class BindProperty {

	/**
	 * 获取行号或者cell 取值
	 */
	@Setter
	private Operation operation;

	/**
	 * field 信息
	 */
	@Setter
	@Getter
	Property property;

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
	private FeatureConfig featureConfig;


	/**
	 * 一个规则绑定一个处理器
	 */
	@Setter
	@Getter
	private BindMapping bindMapping;
	/**
	 * 如果是complex == Complex.Y
	 * 则此处不为空
	 */
	@Setter
	@Getter
	private List<BindProperty> subBindProperties;
	/**
	 * 规则绑定的列
	 */
	@Setter
	@Getter
	private List<BindColumn> bindColumns;

	public int getOrder() {
		return featureConfig.getOrder();
	}

	/**
	 * 是否有效
	 *
	 * @return
	 */
	public boolean isBound() {
		return bindMapping != null;
	}

	/**
	 * 是否为嵌套类型
	 *
	 * @return
	 */
	public boolean isNested() {
		return featureConfig.getComplex() == ComplexEnum.Nested;
	}

	/**
	 * 是否为独占模式
	 *
	 * @return
	 */
	public boolean isExclusive() {
		return featureConfig.getBindType() == BindType.Exclusive;
	}

	/**
	 * 是否为行号读取
	 *
	 * @return
	 */
	public boolean isLineNum() {
		return operation == Operation.LINE_NUM;
	}

	public Method getReadMethod() {
		return property.getReadMethod();
	}

	public Method getWriteMethod() {
		return property.getWriteMethod();
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return property.getPropertyDescriptor();
	}

	public JavaType getType() {
		return property.getFieldType();
	}

	public String getPropertyName() {
		return property.getName();
	}
}
