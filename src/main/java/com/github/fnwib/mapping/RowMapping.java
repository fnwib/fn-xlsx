package com.github.fnwib.mapping;

import com.github.fnwib.mapping.impl.cell.CellMapping;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RowMapping {
	/**
	 * 判断当前row是否为空行
	 *
	 * @param row
	 * @return
	 */
	boolean isEmpty(Row row);

	/**
	 * 当前row是否与规则匹配
	 *
	 * @param row
	 * @return
	 */
	<T> boolean match(Class<T> bindClass, Row row);

	<T> Optional<T> convert(Class<T> bindClass, Row row);

	Map<String, Object> convertToMap(Row row);

	/**
	 * 当前row是否与规则匹配
	 *
	 * @param row
	 * @return
	 */
	Map<BindParam, List<CellMapping>> match(Collection<BindParam> bindParams, Row row);


}
