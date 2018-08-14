package com.github.fnwib.mapper.nested;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.jackson.Json;
import com.github.fnwib.mapper.BindMapper;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.mapper.model.BindProperty;
import com.github.fnwib.model.Content;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 嵌套类型实现
 */
@Slf4j
public class NestedMapper<T> implements BindMapper {

	private List<BindColumn> columns;
	private Class<T> type;
	/**
	 * 支持List<Cell>类型 MappingHelper.listCellType
	 */
	private List<BindProperty> afterJsonHandler;

	/**
	 * 其他
	 */
	private List<BindProperty> flatHandlers;

	public NestedMapper(Class<T> type, List<BindProperty> properties) {
		afterJsonHandler = Lists.newArrayList();
		flatHandlers = Lists.newArrayList();
		columns = Lists.newArrayList();
		this.type = type;
		for (BindProperty bind : properties) {
			if (!bind.isBound()) {
				continue;
			}
			BindMapper mapper = bind.getMapper();
			this.columns.addAll(mapper.getColumns());
			if (bind.isNested()) {
				afterJsonHandler.add(bind);
			} else {
				flatHandlers.add(bind);
			}
		}
	}

	@Override
	public List<BindColumn> getColumns() {
		columns.sort(Comparator.comparing(BindColumn::getIndex));
		return columns;
	}

	@Override
	public Optional<T> getValue(Row fromValue) {
		Map<String, Object> map = Maps.newHashMapWithExpectedSize(flatHandlers.size());
		for (BindProperty property : flatHandlers) {
			BindMapper mapper = property.getMapper();
			mapper.getValue(fromValue).ifPresent(v -> map.put(property.getPropertyName(), v));
		}
		T result = Json.Mapper.convertValue(map, type);
		for (BindProperty property : afterJsonHandler) {
			BindMapper mapper = property.getMapper();
			Optional<?> value = mapper.getValue(fromValue);
			value.ifPresent(v -> setValue(v, property.getWriteMethod(), result));
		}
		return Optional.of(result);
	}

	private void setValue(Object fromValue, Method writeMethod, T toValue) {
		try {
			if (writeMethod == null) {
				return;
			}
			writeMethod.invoke(toValue, fromValue);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("{} set value error {}", toValue.getClass(), e);
			throw new ExcelException(e);
		}
	}

	@Override
	public List<Content> getContents(Object value) {
		List<Content> result = Lists.newArrayListWithCapacity(columns.size());
		for (BindProperty property : afterJsonHandler) {
			if (property.isReadOnly()) {
				continue;
			}
			BindMapper mapper = property.getMapper();
			Object pv = getValue(value, property.getReadMethod());
			result.addAll(mapper.getContents(pv));
		}
		for (BindProperty property : flatHandlers) {
			if (property.isReadOnly()) {
				continue;
			}
			BindMapper mapper = property.getMapper();
			Object pv = getValue(value, property.getReadMethod());
			List<Content> contents = mapper.getContents(pv);
			result.addAll(contents);
		}
		return result;
	}

	private Object getValue(Object fromValue, Method readValue) {
		try {
			if (readValue == null) {
				return null;
			}
			return readValue.invoke(fromValue);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("{} set value error {}", fromValue.getClass(), e);
			throw new ExcelException(e);
		}
	}
}
