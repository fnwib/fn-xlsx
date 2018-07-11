package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.BindType;
import com.github.fnwib.annotation.Complex;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.impl.BindMapping;
import com.github.fnwib.mapping.impl.LineNumMapping;
import com.github.fnwib.mapping.impl.PrimitiveMapping;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.model.BindProperty;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;

public class RowMappingImpl<T> implements RowMapping<T> {

	private static final Logger log = LoggerFactory.getLogger(RowMappingImpl.class);

	private final LocalConfig localConfig;

	private Class<T> type;
	private MappingHelper<T> helper;
	private LongAdder count;

	public RowMappingImpl(Class<T> type) {
		this(type, Context.INSTANCE.getContextConfig());
	}

	public RowMappingImpl(Class<T> type, LocalConfig localConfig) {
		this.type = type;
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
	public boolean match(Row fromValue) {
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
			this.helper = new MappingHelper<>(type, bindProperties);
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
		//按order 顺序绑定
		bindProperties.sort(Comparator.comparing(BindProperty::getOrder));
		for (BindProperty property : bindProperties) {
			if (property.isComplexY()) {
				bind(fromValue, property.getSubBindProperties(), ignoreColumns, bindColumnCount);
				continue;
			}
			FnMatcher fnMatcher = new FnMatcher(property.getMatchConfig(), localConfig);
			List<BindColumn> columns = fnMatcher.match(fromValue, ignoreColumns);
			if (columns.isEmpty()) {
				continue;
			}
			bindColumnCount.increment();
			if (property.isExclusive()) {
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
			if (bind.isComplexY()) {
				List<BindProperty> sub = getBindProperties(property.getFieldType().getRawClass(), level);
				bind.setSubBindProperties(sub);
			}
			result.add(bind);
		}
		return result;
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
			if (property.isLineNum()) {
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
	public Optional<T> readValue(Row fromValue) {
		if (isEmpty(fromValue)) {
			return Optional.empty();
		}
		T convert = helper.convert(fromValue);
		return Optional.of(convert);
	}


	@Override
	public void writeValue(T fromValue, Row toValue) {
		helper.writeValue(fromValue, toValue);
	}


}
