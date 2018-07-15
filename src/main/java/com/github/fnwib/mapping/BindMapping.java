package com.github.fnwib.mapping;

import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
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
	List<ExcelContent> getContents(Object value);

}