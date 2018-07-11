package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.BindType;
import com.github.fnwib.annotation.Complex;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.jackson.Json;
import com.github.fnwib.mapping.impl.BindMapping;
import com.github.fnwib.mapping.impl.CollectionCellMapping;
import com.github.fnwib.mapping.impl.LineNumMapping;
import com.github.fnwib.mapping.impl.PrimitiveMapping;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.model.BindProperty;
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

public class RowMappingImpl implements RowMapping {

	private static final Logger log = LoggerFactory.getLogger(RowMappingImpl.class);

	private final LocalConfig localConfig;

	private List<BindProperty> handlers;
	private LongAdder count;

	public RowMappingImpl() {
		this(Context.INSTANCE.getContextConfig());
	}

	public RowMappingImpl(LocalConfig localConfig) {
		this.localConfig = localConfig;
		this.count = new LongAdder();
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
	public <T> boolean match(Row fromValue, Class<T> type) {
		handlers.clear();
		LongAdder level = new LongAdder();
		LongAdder bindColumnCount = new LongAdder();
		List<BindProperty> bindProperties = getBindProperties(type, level);
		if (level.intValue() >= 3) {
			throw new SettingException("'%s'嵌套层数超过两层", type);
		}
		Set<Integer> ignoreColumns = Sets.newHashSet();
		bind(fromValue, bindProperties, ignoreColumns, bindColumnCount);
		if (bindColumnCount.intValue() > 0) {
			resolve(bindProperties);
			this.handlers = bindProperties;
			return true;
		}
		return false;
	}

	/**
	 * 绑定
	 *
	 * @param fromValue       poi row
	 * @param bindProperties  规则
	 * @param ignoreColumns   不匹配的列 取决于是否配置了独占模式
	 * @param bindColumnCount 规则的有效匹配数量的计数器
	 */
	private void bind(Row fromValue, List<BindProperty> bindProperties, Set<Integer> ignoreColumns, LongAdder bindColumnCount) {
		for (BindProperty property : bindProperties) {
			if (property.getComplex() == Complex.Y) {
				bind(fromValue, property.getSubBindProperties(), ignoreColumns, bindColumnCount);
				continue;
			}
			FnMatcher fnMatcher = new FnMatcher(property.getRule(), localConfig);
			List<BindColumn> columns = fnMatcher.match(fromValue, ignoreColumns);
			if (columns.isEmpty()) {
				continue;
			}
			bindColumnCount.increment();
			if (property.getBindType() == BindType.Exclusive) {
				columns.forEach(i -> ignoreColumns.add(i.getIndex()));
			}
			property.setBindColumns(columns);
		}
	}

	/**
	 * 将类型转成规则
	 *
	 * @param type  类型
	 * @param level 嵌套层级
	 * @return
	 */
	private List<BindProperty> getBindProperties(final Class<?> type, LongAdder level) {
		level.increment();
		List<BindProperty> result = new ArrayList<>();
		for (Property property : BeanResolver.INSTANCE.getProperties(type)) {
			Optional<BindProperty> optional = property.toBindParam();
			if (!optional.isPresent()) {
				continue;
			}
			BindProperty bind = optional.get();
			if (bind.getComplex() == Complex.Y) {
				List<BindProperty> sub = getBindProperties(property.getFieldType().getRawClass(), level);
				bind.setSubBindProperties(sub);
			}
			result.add(bind);
		}
		return result;
	}

	@Override
	public <T> Optional<T> readValue(Row fromValue, Class<T> type) {
		if (isEmpty(fromValue)) {
			return Optional.empty();
		}
		List<BindProperty> cellHandlers = Lists.newArrayList();
		Map<String, Object> formValue = Maps.newHashMapWithExpectedSize(count.intValue());

		List<BindProperty> handlers = this.handlers;
		for (BindProperty property : handlers) {
			if (!property.isRegion(type)) {
				continue;
			}
			String name = property.getPropertyName();
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
		List<BindProperty> properties = this.handlers;
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
	 * setBindMapping
	 *
	 * @param bindProperties
	 */
	private void resolve(List<BindProperty> bindProperties) {
		for (BindProperty property : bindProperties) {
			List<BindColumn> columns = property.getBindColumns();
			BindMapping mapping;
			if (property.getOperation() == Operation.LINE_NUM) {
				mapping = new LineNumMapping(columns);
			} else {
				Collection<ValueHandler> valueHandlers = localConfig.getContentValueHandlers();
				valueHandlers.addAll(property.getValueHandlers());
				JavaType type = property.getType();
				if (type.isMapLikeType()) {
					mapping = Mappings.createMapMapping(type, columns, valueHandlers);
				} else if (type.isCollectionLikeType()) {
					mapping = Mappings.createCollectionMapping(type, columns, valueHandlers);
				} else {
					Optional<PrimitiveMapping> primitiveMapping = Mappings.cratePrimitiveMapping(type, columns, valueHandlers);
					if (primitiveMapping.isPresent()) {
						mapping = primitiveMapping.get();
					} else {
						log.error("-> property is [{}] ,type is [{}] , 匹配到多列 index {}", property.getPropertyName(), type, columns);
						throw new SettingException(String.format("property is %s ,type is %s , 匹配到多列", property.getPropertyName(), type));
					}
				}
			}
			property.setBindMapping(mapping);
		}
	}

	@Override
	public void close() {
		if (handlers != null) {
			handlers.clear();
		}
	}
}
