package com.github.fnwib.mapping.impl;

import com.github.fnwib.mapping.BindColumn;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;

public interface BindMapping {

	/**
	 * 获取所有的绑定的column
	 *
	 * @return
	 */
	List<BindColumn> getColumns();

	/**
	 * poi row to String
	 *
	 * @param row
	 * @return
	 */
	Optional<?> getValue(Row row);

	/**
	 * @param value
	 * @return
	 */
	void setValueToRow(Object value, Row row);

}