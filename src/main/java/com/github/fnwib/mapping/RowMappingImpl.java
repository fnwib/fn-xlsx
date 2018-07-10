package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.jackson.Json;
import com.github.fnwib.mapping.impl.*;
import com.github.fnwib.mapping.impl.cell.CellMapping;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

public class RowMappingImpl implements RowMapping {

	private static final Logger log = LoggerFactory.getLogger(RowMappingImpl.class);

	private final LocalConfig localConfig;

	private Map<Class<?>, List<BindProperty>> handlers;
	private LongAdder count;

	public RowMappingImpl() {
		this(Context.INSTANCE.getContextConfig());
	}

	public RowMappingImpl(LocalConfig localConfig) {
		this.localConfig = localConfig;
		this.count = new LongAdder();
		this.handlers = Maps.newHashMap();
	}

	@Override
	public boolean isEmpty(Row row) {
		if (row == null) {
			return true;
		}
		for (Cell cell : row) {
			if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public <T> boolean match(Row fromValue, Class<T> type, boolean ignore) {
		List<BindProperty> repeat = this.handlers.getOrDefault(type, Collections.emptyList());
		if (!repeat.isEmpty()) {
			this.handlers.remove(type);
		}
		Set<Integer> ignoreColumns = Sets.newHashSet();
		if (ignore) {
			this.handlers.forEach((_type, properties) -> {
				List<Integer> columns = properties.stream()
						.map(BindProperty::getBindMapping)
						.map(BindMapping::getCellMappings)
						.flatMap(List::stream)
						.map(CellMapping::getColumn)
						.collect(Collectors.toList());
				ignoreColumns.addAll(columns);
			});
		}
		List<BindProperty> bindProperties = BeanResolver.INSTANCE.getProperties(type)
				.stream()
				.map(Property::toBindParam)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toList());

		Map<BindProperty, List<BindColumn>> match = bind(bindProperties, fromValue, ignoreColumns);
		LongAdder count = new LongAdder();
		match.forEach((_ignore, columns) -> count.add(columns.size()));
		if (count.intValue() > 0) {
			this.count.add(count.intValue());
			List<BindProperty> properties = resolve(match);
			this.handlers.put(type, properties);
			return true;
		}
		return false;
	}

	@Override
	public <T> Optional<T> readValue(Row fromValue, Class<T> type) {
		if (isEmpty(fromValue)) {
			return Optional.empty();
		}
		List<BindProperty> cellHandlers = Lists.newArrayList();
		Map<String, Object> formValue = Maps.newHashMapWithExpectedSize(count.intValue());
		List<BindProperty> handlers = this.handlers.getOrDefault(type, Collections.emptyList());
		for (BindProperty property : handlers) {
			if (!property.isRegion(type)) {
				continue;
			}
			String name = property.getName();
			BindMapping mapping = property.getBindMapping();
			if (CollectionCellMapping.class == mapping.getClass()) {
				cellHandlers.add(property);
			} else {
				Optional<?> value = mapping.getValue(fromValue);
				value.ifPresent(v -> formValue.put(name, v));
			}
		}
		if (formValue.isEmpty() && cellHandlers.isEmpty()) {
			return Optional.empty();
		}
		T t = Json.Mapper.convertValue(formValue, type);
		setValue(cellHandlers, fromValue, t);
		return Optional.of(t);
	}

	private <T> void setValue(List<BindProperty> properties, Row row, T toValue) {
		try {
			for (BindProperty property : properties) {
				BindMapping bindMapping = property.getBindMapping();
				CollectionCellMapping mapping = (CollectionCellMapping) bindMapping;
				Optional<List<Cell>> value = mapping.getValue(row);
				if (value.isPresent()) {
					Method writeMethod = property.getWriteMethod();
					writeMethod.invoke(toValue, value.get());
				}
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("{} set value error {}", toValue.getClass(), e);
			throw new ExcelException(e);
		}
	}


	@Override
	public <T> boolean writeValue(T fromValue, Row toValue) {
		List<BindProperty> properties = this.handlers.getOrDefault(fromValue, Collections.emptyList());
		try {
			for (BindProperty property : properties) {
				Method readMethod = property.getReadMethod();
				Object value = readMethod.invoke(fromValue);
				BindMapping bindMapping = property.getBindMapping();
				bindMapping.setValueToRow(value, toValue);
			}
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("write value error {}", e);
			throw new ExcelException(e);
		}
		return false;
	}

	/**
	 * 规则与title通过匹配进行绑定
	 *
	 * @param bindParams 规则
	 * @param row        poi Row
	 */
	private Map<BindProperty, List<BindColumn>> bind(Collection<BindProperty> bindParams, Row row, Set<Integer> ignoreColumns) {
		if (isEmpty(row)) {
			return Collections.emptyMap();
		}
		Map<BindProperty, List<BindColumn>> map = Maps.newHashMapWithExpectedSize(bindParams.size());
		for (BindProperty bindParam : bindParams) {
			FnMatcher fnMatcher = new FnMatcher(bindParam, localConfig);
			List<BindColumn> columns = fnMatcher.match(row, ignoreColumns);
			map.put(bindParam, columns);
		}
		return map;
	}

	private List<BindProperty> resolve(Map<BindProperty, List<BindColumn>> bound) {
		if (bound.isEmpty()) {
			return Collections.emptyList();
		}
		List<BindProperty> result = Lists.newArrayListWithCapacity(bound.size());
		bound.forEach((bindParam, columns) -> {
			BindMapping mapping;
			if (bindParam.getOperation() == Operation.LINE_NUM) {
				mapping = new LineNumMapping();
			} else {
				Collection<ValueHandler> valueHandlers = localConfig.getContentValueHandlers();
				valueHandlers.addAll(bindParam.getValueHandlers());
				JavaType type = bindParam.getType();
				if (type.isMapLikeType()) {
					mapping = new MapMapping(type.getKeyType(), type.getContentType(), columns, valueHandlers);
				} else if (type.isCollectionLikeType()) {
					if (Cell.class.isAssignableFrom(type.getContentType().getRawClass())) {
						mapping = new CollectionCellMapping(columns);
					} else {
						mapping = new CollectionMapping(type.getContentType(), columns, valueHandlers);
					}
				} else {
					if (columns.isEmpty()) {
						return;
					} else if (columns.size() == 1) {
						mapping = new PrimitiveMapping(type, columns.get(0), valueHandlers);
					} else {
						log.error("-> property is [{}] ,type is [{}] , 匹配到多列 index {}", bindParam.getName(), type, columns);
						String format = String.format("property is %s ,type is %s , 匹配到多列", bindParam.getName(), type);
						throw new SettingException(format);
					}
				}
			}
			bindParam.setBindMapping(mapping);
			result.add(bindParam);
		});
		return result;
	}

	@Override
	public void close() {
		if (handlers != null) {
			handlers.clear();
		}
	}
}
