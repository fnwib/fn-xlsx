package com.github.fnwib.mapper.flat;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.model.BindColumn;

import java.util.List;
import java.util.Objects;

/**
 * Map and Collection impl
 */
public abstract class AbstractContainerMapper implements FlatMapper {

	String name;
	List<BindColumn> columns;

	public AbstractContainerMapper(String name, List<BindColumn> columns) {
		this.name = Objects.requireNonNull(name);
		this.columns = columns;
	}

	@Override
	public List<BindColumn> getColumns() {
		return columns;
	}

	public void check(int size) {
		if (size > columns.size()) {
			throw new ExcelException(" '%s' 允许写入数量'%s',实际数量'%s'", name, columns.size(), size);
		}
	}

}
