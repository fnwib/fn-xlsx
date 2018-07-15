package com.github.fnwib.mapper.flat;

import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Collection and Map impl
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

	@Override
	abstract public Optional<?> getValue(Row row);

	@Override
	abstract public List<ExcelContent> getContents(Object value);
}
