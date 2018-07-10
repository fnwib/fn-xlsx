package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.jackson.Json;
import com.github.fnwib.mapping.impl.*;
import com.github.fnwib.mapping.impl.cell.CellMapping;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Stream;

public class RowMappingImpl implements RowMapping {

	private static final Logger log = LoggerFactory.getLogger(RowMappingImpl.class);

	private final LocalConfig localConfig;

	private Map<BindParam, BindMapping> handlers;
	private int count;

	public RowMappingImpl() {
		this(Context.INSTANCE.getContextConfig());
	}

	public RowMappingImpl(LocalConfig localConfig) {
		this.localConfig = localConfig;
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
	public <T> boolean match(Class<T> bindClass, Row row) {
		Collection<BindParam> bindParams = Lists.newArrayList();
		List<Property> properties = BeanResolver.INSTANCE.getProperties(bindClass);
		for (Property property : properties) {
			property.toBindParam().ifPresent(p -> bindParams.add(p));
		}
		Map<BindParam, List<Integer>> match = bind(bindParams, row);
		LongAdder count = new LongAdder();
		match.forEach((_ignore, columns) -> count.add(columns.size()));
		if (count.intValue() > 0) {
			this.count = count.intValue();
			this.handlers = resolve(match);
			return true;
		}
		return false;
	}


	@Override
	public <T> Optional<T> convert(Class<T> bindClass, Row row) {
		if (isEmpty(row)) {
			return Optional.empty();
		}
		Map<String, Object> formValue = Maps.newHashMapWithExpectedSize(count);
		handlers.forEach((bindParam, mapping) -> {
			Optional<?> value = mapping.getValue(row);
			value.ifPresent(v -> formValue.put(bindParam.getName(), v));
		});
		T t = Json.Mapper.convertValue(formValue, bindClass);
		return Optional.of(t);
	}

	private Map<BindParam, BindMapping> resolve(Map<BindParam, List<Integer>> bound) {
		if (bound.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<BindParam, BindMapping> result = Maps.newHashMapWithExpectedSize(bound.size());
		bound.forEach((bindParam, columns) -> {
			BindMapping mapping;
			if (bindParam.getOperation() == Operation.LINE_NUM) {
				mapping = new LineNumMapping();
			} else {
				Collection<ValueHandler> valueHandlers = localConfig.getContentValueHandlers();
				bindParam.getValueHandlers().forEach(valueHandler -> valueHandlers.add(valueHandler));
				JavaType type = bindParam.getType();
				if (type.isMapLikeType()) {
					mapping = new MapIndexKeyMapping(type.getKeyType(), type.getContentType(), columns, valueHandlers);
				} else if (type.isCollectionLikeType()) {
					mapping = new CollectionMapping(type.getContentType(), columns, valueHandlers);
				} else {
					if (columns.isEmpty()) {
						return;
					} else if (columns.size() == 1) {
						mapping = new PrimitiveMapping(type, columns.get(0), valueHandlers);
					} else {
						log.error("-> property is [{}] ,type is [{}]", bindParam.getName(), type);
						String format = String.format("property is %s ,type is %s , 匹配到多列 index {}", bindParam.getName(), type, columns);
						throw new SettingException(format);
					}
				}
			}
			result.put(bindParam, mapping);
		});
		return result;
	}

	private Map<BindParam, List<Integer>> bind(Collection<BindParam> bindParams, Row row) {
		if (isEmpty(row)) {
			return Collections.emptyMap();
		}
		Map<BindParam, List<Integer>> map = Maps.newHashMapWithExpectedSize(bindParams.size());
		for (BindParam bindParam : bindParams) {
			FnMatcher fnMatcher = new FnMatcher(bindParam, localConfig);
			List<Integer> columns = fnMatcher.match(row);
			map.put(bindParam, columns);
		}
		return map;
	}

	/**
	 * 当前row是否与规则匹配
	 *
	 * @param row
	 * @return
	 */
	@Override
	public Map<BindParam, List<CellMapping>> match(Collection<BindParam> bindParams, Row row) {
		if (isEmpty(row)) {
			return Collections.emptyMap();
		}
		Map<BindParam, List<Integer>> bind = bind(bindParams, row);
		Map<BindParam, BindMapping> resolve = resolve(bind);
		if (bind.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<BindParam, List<CellMapping>> result = Maps.newHashMapWithExpectedSize(bind.size());
		resolve.forEach((bindParam, mapping) -> result.put(bindParam, mapping.getCellMappings()));
		return result;
	}


}
