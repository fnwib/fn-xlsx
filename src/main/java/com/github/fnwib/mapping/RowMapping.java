package com.github.fnwib.mapping;

import org.apache.poi.ss.usermodel.Row;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public interface RowMapping {

	<T> Map<BindParam, List<Integer>> match(Class<T> bindClass, Row row);

	/**
	 * 当前row是否与规则匹配
	 *
	 * @param row
	 * @return
	 */
	Map<BindParam, List<Integer>> match(Collection<BindParam> bindParams, Row row);

	Map<BindParam, List<BindMapping>> resolve(Map<BindParam, List<Integer>> bound);
}
