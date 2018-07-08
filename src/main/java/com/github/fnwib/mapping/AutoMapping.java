package com.github.fnwib.mapping;

import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.valuehandler.ValueFormatter;

import java.util.List;


public interface AutoMapping {
	/**
	 * mapping的名称
	 */
	String getName();

	/**
	 * <p>
	 * LINE_NUM 字段是excel行号 从1开始
	 * DEFAULT Value
	 * <p>
	 *
	 * @return
	 */
	Operation getOperation();


	/**
	 * title 前缀
	 * 完整匹配-不支持正则
	 *
	 * @return
	 */
	String getTitle();

	/**
	 * 绑定的列
	 */
	List<Integer> getBind();


	/**
	 * 值处理
	 */
	<T> List<ValueFormatter<T>> getValueFormat();

}
