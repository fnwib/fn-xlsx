package com.github.fnwib.mapper;

import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;

public interface BindMapper {

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