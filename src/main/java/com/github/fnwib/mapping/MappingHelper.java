package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.jackson.Json;
import com.github.fnwib.mapping.impl.BindMapping;
import com.github.fnwib.mapping.impl.CollectionCellMapping;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.model.BindProperty;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
public class MappingHelper<T> {
	private static final JavaType listCellType = TypeFactory.defaultInstance().constructCollectionType(List.class, Cell.class);

	private List<BindProperty> cellHandlers;

	private List<BindProperty> customHandlers;

	private Map<Method, MappingHelper> complexMappingHelper;

	private Class<T> type;

	private int count;

	boolean writeChecked;

	public MappingHelper(Class<T> type, List<BindProperty> handlers) {
		this.type = type;
		this.count = handlers.size();
		cellHandlers = Lists.newArrayList();
		complexMappingHelper = Maps.newHashMap();
		customHandlers = Lists.newArrayList();
		for (BindProperty handler : handlers) {
			if (handler.getType() == listCellType) {
				cellHandlers.add(handler);
				continue;
			}
			if (handler.isComplexY()) {
				List<BindProperty> subBindProperties = handler.getSubBindProperties();
				Method writeMethod = handler.getWriteMethod();
				Class<?> rawClass = handler.getType().getRawClass();
				MappingHelper<?> helper = new MappingHelper(rawClass, subBindProperties);
				complexMappingHelper.put(writeMethod, helper);
				continue;
			}
			customHandlers.add(handler);
		}
	}

	public T convert(Row fromValue) {
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
		complexMappingHelper.forEach((writeMethod, mappingHelper) -> {
			Object convert = mappingHelper.convert(fromValue);
			setValue(convert, writeMethod, value);
		});
		return value;
	}

	private void setValue(Object fromValue, Method writeMethod, T toValue) {
		try {
			writeMethod.invoke(toValue, fromValue);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("{} set value error {}", toValue.getClass(), e);
			throw new ExcelException(e);
		}
	}

	private boolean checked() {
		if (!writeChecked) {
			Set<Integer> indexes = Sets.newHashSet();
			for (BindProperty h : cellHandlers) {
				BindMapping mapping = h.getBindMapping();
				List<BindColumn> columns = mapping.getColumns();
				for (BindColumn column : columns) {
					if (indexes.contains(column.getIndex())) {
						throw new SettingException("写出数据重复");
					} else {
						indexes.add(column.getIndex());
					}
				}
			}
			writeChecked = true;
		}
		return true;
	}

	public void writeValue(T fromValue, Row toValue) {
		checked();
		for (BindProperty property : cellHandlers) {
			BindMapping mapping = property.getBindMapping();
			Object value = getValue(fromValue, property.getReadMethod());
			mapping.setValueToRow(value, toValue);
		}
	}

	private Object getValue(T fromValue, Method readValue) {
		try {
			return readValue.invoke(fromValue);
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("{} set value error {}", fromValue.getClass(), e);
			throw new ExcelException(e);
		}
	}
}
