package com.github.fnwib.mapper;

import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.model.Content;
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
	 * 将POI一行数据row转成一个对象
	 *
	 * @param row
	 * @return
	 */
	Optional<?> getValue(Row row);

	/**
	 * 将一个对象装成一组Content
	 *
	 * @param value
	 * @return
	 */
	List<Content> getContents(Object value);

}