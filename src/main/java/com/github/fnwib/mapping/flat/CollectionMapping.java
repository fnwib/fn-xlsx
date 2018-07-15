package com.github.fnwib.mapping.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapping.cell.AbstractCellStringMapping;
import com.github.fnwib.mapping.BindMapping;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.Mappings;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

/**
 * Collection impl
 * <p>
 * Tip ！ contentType 应该是集合元素类型
 * 它只能是 Number String Enum 之类的单一映射的结构
 * 不能是Collection 或Map
 */
public class CollectionMapping implements FlatMapping {

	private AbstractCellStringMapping mapping;
	private List<BindColumn> columns;

	public CollectionMapping(JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		this.mapping = Mappings.createSimpleMapping(contentType, valueHandlers);
		this.columns = columns;
	}

	@Override
	public List<BindColumn> getColumns() {
		return columns;
	}

	@Override
	public Optional<Collection<String>> getValue(Row row) {
		if (columns.isEmpty()) {
			return Optional.empty();
		}
		Collection<String> result = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Optional<String> value = mapping.getValue(column.getIndex(), row);
			result.add(value.orElse(StringUtils.EMPTY));
		}
		return Optional.of(result);
	}

	@Override
	public List<ExcelContent> getContents(Object value) {
		List<Object> objects = Objects.nonNull(value) ? (List<Object>) value : Collections.emptyList();
		if (objects.size() > columns.size()) {
			String format = String.format("当前集合数量'%s'大于允许写入数量'%s'", objects.size(), columns.size());
			throw new ExcelException(format);
		}
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(columns.size());
		int size = objects.size();
		int i = 0;
		for (BindColumn column : columns) {
			Object v = null;
			if (i < size) {
				v = objects.get(i);
				i++;
			}
			Integer index = column.getIndex();
			String val = Objects.isNull(v) ? null : v.toString();
			contents.add(new ExcelContent(index, val));

		}
		return contents;
	}
}
