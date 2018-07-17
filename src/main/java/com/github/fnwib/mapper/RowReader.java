package com.github.fnwib.mapper;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.model.ExcelHeader;
import com.github.fnwib.model.RowExcelContent;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;

/**
 * row 转对象的实现
 */
public interface RowReader<T> extends LineReader<T> {
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
	 * @return
	 */
	boolean match(Row fromValue);

	boolean match(List<ExcelHeader> headers);

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
	RowExcelContent convert(T fromValue);

}
