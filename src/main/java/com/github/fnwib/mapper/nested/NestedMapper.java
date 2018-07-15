package com.github.fnwib.mapper.nested;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.jackson.Json;
import com.github.fnwib.mapper.BindMapper;
import com.github.fnwib.mapper.flat.CollectionCellMapper;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.mapper.model.BindProperty;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 嵌套类型实现
 */
@Slf4j
public class NestedMapper<T> implements BindMapper {

	private static final JavaType LIST_CELL_TYPE = TypeFactory.defaultInstance().constructCollectionType(List.class, Cell.class);


	private List<BindColumn> columns;
	private Class<T> type;
	/**
	 * 支持List<Cell>类型 MappingHelper.listCellType
	 */
	private List<BindProperty> cellHandlers;

	/**
	 * 其他
	 */
	private List<BindProperty> flatHandlers;

	//有效的绑定的属性
	private int count;

	public NestedMapper(JavaType type, List<BindProperty> properties) {
		if (type.isPrimitive()) {
			throw new SettingException("不支持这样的嵌套类型");
		}
		cellHandlers = Lists.newArrayList();
		flatHandlers = Lists.newArrayList();
		columns = Lists.newArrayList();
		this.type = (Class<T>) type.getRawClass();
		for (BindProperty bind : properties) {
			if (!bind.isBound()) {
				continue;
			}
			BindMapper mapper = bind.getMapper();
			this.columns.addAll(mapper.getColumns());
			if (bind.getType() == LIST_CELL_TYPE) {
				cellHandlers.add(bind);
			} else {
				flatHandlers.add(bind);
			}
		}
		this.count = cellHandlers.size() + flatHandlers.size();
	}

	@Override
	public List<BindColumn> getColumns() {
		return columns;
	}

	@Override
	public Optional<T> getValue(Row fromValue) {
		Map<String, Object> map = Maps.newHashMapWithExpectedSize(count);
		for (BindProperty handler : flatHandlers) {
			BindMapper mapper = handler.getMapper();
			mapper.getValue(fromValue).ifPresent(v -> map.put(handler.getPropertyName(), v));
		}
		T value = Json.Mapper.convertValue(map, type);
		for (BindProperty handler : cellHandlers) {
			CollectionCellMapper mapper = (CollectionCellMapper) handler.getMapper();
			Optional<List<Cell>> cells = mapper.getValue(fromValue);
			if (cells.isPresent()) {
				setValue(cells.get(), handler.getWriteMethod(), value);
			}
		}
		return Optional.of(value);
	}

	private void setValue(Object fromValue, Method writeMethod, T toValue) {
		try {
			writeMethod.invoke(toValue, fromValue);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("{} set value error {}", toValue.getClass(), e);
			throw new ExcelException(e);
		}
	}

	@Override
	public List<ExcelContent> getContents(Object value) {
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindProperty property : cellHandlers) {
			BindMapper mapper = property.getMapper();
			Object pv = getValue(value, property.getReadMethod());
			contents.addAll(mapper.getContents(pv));
		}
		for (BindProperty property : flatHandlers) {
			BindMapper mapper = property.getMapper();
			Object pv = getValue(value, property.getReadMethod());
			contents.addAll(mapper.getContents(pv));
		}
		return contents;
	}

	private Object getValue(Object fromValue, Method readValue) {
		try {
			return readValue.invoke(fromValue);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("{} set value error {}", fromValue.getClass(), e);
			throw new ExcelException(e);
		}
	}
}
