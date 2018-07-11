package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

/**
 * row 转对象的实现
 */
public interface RowMapping extends AutoCloseable {
	/**
	 * 判断当前row是否为空行
	 *
	 * @param fromValue
	 * @return
	 */
	boolean isEmpty(Row fromValue);

	/**
	 * 检查row是否与规则匹配
	 * <p>
	 * BFS 绑定
	 *
	 * @param fromValue poi row
	 * @param type      type
	 * @param <T>
	 * @return
	 */
	<T> boolean match(Row fromValue, Class<T> type);

	/**
	 * row convert to T
	 *
	 * @param fromValue poi row
	 * @param type      类型
	 * @return
	 */
	<T> Optional<T> readValue(Row fromValue, Class<T> type);

	/**
	 * write T to empty Row
	 *
	 * @param fromValue value
	 * @param toValue   empty row
	 * @return
	 */
	<T> boolean writeValue(T fromValue, Row toValue);

	/**
	 * 为了支持一个row转成多个对象
	 * (多个对象转成一个row)
	 * <p>
	 * 会将每次匹配的结果都存起来
	 * <p>
	 * 调用此方法清除所有的匹配结果
	 */
	@Override
	void close();
}
