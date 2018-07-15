package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapper.cell.CellMapping;
import com.github.fnwib.mapper.cell.NumberMapping;
import com.github.fnwib.mapper.cell.SimpleMapping;
import com.github.fnwib.mapper.cell.StringMapping;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * 一个字段绑定一个column的实现
 */
public class PrimitiveMapper implements FlatMapper {


	private CellMapping cellMapping;
	private BindColumn column;

	public PrimitiveMapper(JavaType javaType, BindColumn column, Collection<ValueHandler> valueHandlers) {
		Class<?> rawClass = javaType.getRawClass();
		if (String.class == rawClass) {
			cellMapping = new StringMapping(valueHandlers);
		} else if (Number.class.isAssignableFrom(rawClass)) {
			cellMapping = new NumberMapping();
		} else {
			cellMapping = new SimpleMapping(javaType);
		}
		this.column = column;
	}

	@Override
	public List<BindColumn> getColumns() {
		return Lists.newArrayList(column);
	}

	@Override
	public Optional<?> getValue(Row row) {
		return cellMapping.getValue(column.getIndex(), row);
	}

	@Override
	public List<ExcelContent> getContents(Object value) {
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(1);
		Integer index = column.getIndex();
		String val = Objects.isNull(value) ? null : value.toString();
		contents.add(new ExcelContent(index, val));
		return contents;
	}
}
