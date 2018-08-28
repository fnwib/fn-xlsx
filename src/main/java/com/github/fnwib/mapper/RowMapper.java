package com.github.fnwib.mapper;

import com.github.fnwib.model.Content;
import com.github.fnwib.model.Header;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;

/**
 * row 转对象的实现
 */
public interface RowMapper<T> {

	/**
	 * 创建一个新的RowMapper
	 *
	 * @param from
	 * @param to
	 * @return
	 */
	RowMapper<T> of(int from, int to);

	/**
	 * 判断当前row是否为空行
	 * <p>
	 *
	 * @param fromValue poi row
	 * @return row是否为空
	 */
	boolean isEmpty(Row fromValue);

	/**
	 * 检查row是否与规则匹配
	 * <p>
	 * DFS 绑定
	 *
	 * @param fromValue poi row
	 * @return 是否匹配成功
	 */
	boolean match(Row fromValue);

	boolean match(List<Header> headers);

	/**
	 * @param fromValue poi row
	 * @return skip 之后的cell 转成T
	 */
	Optional<T> convert(Row fromValue);

	/**
	 * T to contents
	 *
	 * @param fromValue value
	 */
	List<Content> convert(T fromValue);

}
