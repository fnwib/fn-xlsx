package com.github.fnwib.mapping.impl;

import com.github.fnwib.mapping.impl.cell.CellMapping;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;

public interface BindMapping {

	/**
	 * 获取所有的cellMapping
	 *
	 * @return
	 */
	List<CellMapping> getCellMappings();

	/**
	 * poi row to String
	 *
	 * @param row
	 * @return
	 */
	Optional<?> getValue(Row row);

}