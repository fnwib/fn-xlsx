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
	 * @return 跳过的columnIndex default -1
	 */
	int getSkip();

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
	 * DFS 绑定
	 *
	 * @param fromValue poi row
	 * @return
	 */
	boolean match(Row fromValue);

	boolean match(List<Header> headers);

	/**
	 * row convert to T
	 *
	 * @param fromValue poi row
	 * @return
	 */
	Optional<T> convert(Row fromValue);

	/**
	 * T to contents
	 *
	 * @param fromValue value
	 */
	List<Content> convert(T fromValue);

}
