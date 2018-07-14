package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.jackson.Json;
import com.github.fnwib.mapping.Mappings;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.model.BindProperty;
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
 * 嵌套类型处理
 */
@Slf4j
public class NestedMapping<T> implements BindMapping {

	private List<BindColumn> columns;
	private Class<T> type;
	/**
	 * 支持List<Cell>类型 MappingHelper.listCellType
	 */
	private List<BindProperty> cellHandlers;

	/**
	 * 其他
	 */
	private List<BindProperty> customHandlers;

	//有效的绑定的属性
	private int count;

	public NestedMapping(JavaType type, List<BindProperty> properties, LocalConfig config) {
		if (type.isPrimitive()) {
			throw new SettingException("不支持这样的嵌套类型");
		}
		cellHandlers = Lists.newArrayList();
		customHandlers = Lists.newArrayList();
		columns = Lists.newArrayList();
		this.type = (Class<T>) type.getRawClass();
		for (BindProperty bind : properties) {
			Mappings.createMapping(bind, config);
			if (!bind.isBound()) {
				continue;
			}
			List<BindColumn> columns = bind.getBindColumns();
			if (columns == null) {
				log.error("{} {} columns is null",bind.getPropertyName(), type);
				continue;
			}
			this.columns.addAll(columns);
			if (bind.getType() == Constant.LIST_CELL_TYPE) {
				cellHandlers.add(bind);
			} else {
				customHandlers.add(bind);
			}
		}
		this.count = cellHandlers.size() + customHandlers.size();
	}

	@Override
	public List<BindColumn> getColumns() {
		return columns;
	}

	@Override
	public Optional<T> getValue(Row fromValue) {
		Map<String, Object> map = Maps.newHashMapWithExpectedSize(count);
		for (BindProperty handler : customHandlers) {
			BindMapping mapping = handler.getBindMapping();
			mapping.getValue(fromValue).ifPresent(v -> map.put(handler.getPropertyName(), v));
		}
		T value = Json.Mapper.convertValue(map, type);
		for (BindProperty handler : cellHandlers) {
			CollectionCellMapping mapping = (CollectionCellMapping) handler.getBindMapping();
			Optional<List<Cell>> cells = mapping.getValue(fromValue);
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
			BindMapping mapping = property.getBindMapping();
			Object pv = getValue(value, property.getReadMethod());
			contents.addAll(mapping.getContents(pv));
		}
		for (BindProperty property : customHandlers) {
			BindMapping mapping = property.getBindMapping();
			Object pv = getValue(value, property.getReadMethod());
			contents.addAll(mapping.getContents(pv));
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
