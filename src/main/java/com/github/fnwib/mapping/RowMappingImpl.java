package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.MapType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapping.impl.NumberMapping;
import com.github.fnwib.mapping.impl.SimpleMapping;
import com.github.fnwib.mapping.impl.StringMapping;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class RowMappingImpl implements RowMapping {

	private LocalConfig localConfig;

	public RowMappingImpl() {
		this(Context.INSTANCE.getContextConfig());
	}

	public RowMappingImpl(LocalConfig localConfig) {
		this.localConfig = localConfig;
	}

	/**
	 * 判断当前row是否为空行
	 *
	 * @param row
	 * @return
	 */
	private boolean isEmpty(Row row) {
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
	public <T> Map<BindParam, List<Integer>> match(Class<T> bindClass, Row row) {
		Collection<BindParam> bindParams = Lists.newArrayList();
		List<Property> properties = BeanResolver.INSTANCE.getProperties(bindClass);
		for (Property property : properties) {
			property.toBindParam().ifPresent(p -> bindParams.add(p));
		}
		return match(bindParams, row);
	}

	/**
	 * 当前row是否与规则匹配
	 *
	 * @param row
	 * @return
	 */
	@Override
	public Map<BindParam, List<Integer>> match(Collection<BindParam> bindParams, Row row) {
		if (isEmpty(row)) {
			return Collections.emptyMap();
		}
		Map<BindParam, List<Integer>> map = Maps.newHashMapWithExpectedSize(bindParams.size());
		for (BindParam bindParam : bindParams) {
			FnMatcher fnMatcher = new FnMatcher(bindParam, localConfig);
			List<Integer> columns = fnMatcher.match(row);
			if (columns.isEmpty()) {
				continue;
			}
			map.put(bindParam, columns);
		}
		return map;
	}

	@Override
	public Map<BindParam, List<BindMapping>> resolve(Map<BindParam, List<Integer>> bound) {
		if (bound.isEmpty()) {
			return Collections.emptyMap();
		}
		Map<BindParam, List<BindMapping>> result = Maps.newHashMapWithExpectedSize(bound.size());
		bound.forEach((bindParam, columns) -> {
			Collection<ValueHandler> valueHandlers = localConfig.getContentValueHandlers();
			bindParam.getValueHandlers().forEach(valueHandler -> valueHandlers.add(valueHandler));
			List<BindMapping> bindMapping;
			JavaType type = bindParam.getType();
			if (type.isContainerType()) {
				bindMapping = createBindMapping(type.getContentType(), valueHandlers, columns);
			} else {
				bindMapping = createBindMapping(type, valueHandlers, columns);
			}
			result.put(bindParam, bindMapping);
		});
		return result;
	}

	private List<BindMapping> createBindMapping(JavaType contentType, Collection<ValueHandler> valueHandlers, List<Integer> columns) {
		List<BindMapping> bindMappings;
		Class<?> rawClass = contentType.getRawClass();
		if (String.class == rawClass) {
			bindMappings = columns.stream().map(c -> new StringMapping(c, valueHandlers)).collect(Collectors.toList());
		} else if (Number.class.isAssignableFrom(rawClass)) {
			bindMappings = columns.stream().map(NumberMapping::new).collect(Collectors.toList());
		} else {
			bindMappings = columns.stream().map(c -> new SimpleMapping(contentType, c)).collect(Collectors.toList());
		}
		return bindMappings;
	}

}
