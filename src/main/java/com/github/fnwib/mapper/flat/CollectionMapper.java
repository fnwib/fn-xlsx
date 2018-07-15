package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.Mappings;
import com.github.fnwib.mapper.cell.AbstractCellStringMapping;
import com.github.fnwib.mapper.model.BindColumn;
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
public class CollectionMapper extends AbstractContainerMapper {

	private AbstractCellStringMapping mapping;

	public CollectionMapper(String name, JavaType contentType, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		super(name, columns);
		this.mapping = Mappings.createSimpleMapping(contentType, valueHandlers);
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
		List<Object> values = Objects.nonNull(value) ? (List<Object>) value : Collections.emptyList();
		if (values.size() > columns.size()) {
			throw new ExcelException("[%s]允许写入数量'%s',实际数量'%s'大于", name, columns.size(), values.size());

		}
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(columns.size());
		int size = values.size();
		int i = 0;
		for (BindColumn column : columns) {
			String val = null;
			if (i < size) {
				Object v = values.get(i);
				if (val != null) {
					val = v.toString();
				}
				i++;
			}
			Integer index = column.getIndex();
			contents.add(new ExcelContent(index, val));
		}
		return contents;
	}
}
