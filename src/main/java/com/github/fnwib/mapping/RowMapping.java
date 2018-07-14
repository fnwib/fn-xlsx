package com.github.fnwib.mapping;

import com.github.fnwib.write.model.ExcelContent;
import com.github.fnwib.write.model.ExcelHeader;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;

/**
 * row 转对象的实现
 */
public interface RowMapping<T> {
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

	MappingHelper<T> getMappingHelper();

	/**
	 * row convert to T
	 *
	 * @param fromValue poi row
	 * @return
	 */
	Optional<T> readValue(Row fromValue);

	/**
	 * write T to empty Row
	 *
	 * @param fromValue value
	 */
	List<ExcelContent> writeValue(T fromValue);


}
