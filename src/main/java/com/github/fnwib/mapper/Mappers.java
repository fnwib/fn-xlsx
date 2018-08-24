package com.github.fnwib.mapper;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.jackson.Sequence;
import com.github.fnwib.mapper.flat.*;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.mapper.model.BindProperty;
import com.github.fnwib.mapper.model.MatchConfig;
import com.github.fnwib.mapper.nested.NestedMapper;
import com.github.fnwib.model.Header;
import com.github.fnwib.plugin.ValueHandler;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
public class Mappers {

	private Mappers() {
	}

	/**
	 * @param type
	 * @param config
	 * @param headers
	 * @param <T>
	 * @return
	 */
	public static <T> NestedMapper<T> createNestedMapper(Class<T> type, LocalConfig config, List<Header> headers) {
		return createNestedMapper(type, config, headers, Sets.newHashSet(), 1);
	}

	/**
	 * @param type             嵌套类型
	 * @param config           LocalConfig
	 * @param headers          Headers(等价于org.apache.poi.ss.usermodel.Row)
	 * @param exclusiveColumns 独占模式的列
	 * @param <T>              嵌套类型
	 * @return NestedMapping
	 */
	private static <T> NestedMapper<T> createNestedMapper(Class<T> type, LocalConfig config, List<Header> headers, Set<Integer> exclusiveColumns, int level) {
		canSupport(type);
		if (level > config.getMaxNestLevel()) {
			throw new SettingException("嵌套层数超过'%s'层,当前对象为'%s'", config.getMaxNestLevel(), type);
		}
		List<BindProperty> properties = BeanResolver.INSTANCE.getProperties(type).stream().map(Property::toBindParam)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.sorted(Comparator.comparing(BindProperty::getOrder))
				.collect(Collectors.toList());
		for (BindProperty property : properties) {
			if (property.isNested()) {
				NestedMapper<?> nestedMapping = createNestedMapper(property.getRawClass(), config, headers, exclusiveColumns, level++);
				property.setMapper(nestedMapping);
				continue;
			}
			List<BindColumn> columns = match(property, config, headers, exclusiveColumns);
			FlatMapper flatMapper = createFlatMapper(property, columns, config);
			property.setMapper(flatMapper);
		}
		return new NestedMapper<>(type, properties);
	}

	public static void canSupport(Class<?> type) {
		if (type.isPrimitive()) {
			throw new SettingException("不支持基本类型'%s'", type);
		}
		if (type.isInterface()) {
			throw new SettingException("不支持 abstract、interface'%s'", type);
		}
		List<Property> properties = BeanResolver.INSTANCE.getProperties(type);
		if (properties.isEmpty()) {
			throw new SettingException("%s 不支持没有getter、setter的类", type);
		}
	}

	/**
	 * 属性与Header匹配
	 *
	 * @param property         嵌套类型
	 * @param config           LocalConfig
	 * @param headers          Headers(等价于org.apache.poi.ss.usermodel.Row)
	 * @param exclusiveColumns 独占模式的列
	 * @return 匹配到的列集合
	 */
	private static List<BindColumn> match(BindProperty property, LocalConfig config, List<Header> headers, Set<Integer> exclusiveColumns) {
		MatchConfig matchConfig = property.getMatchConfig();
		FnMatcher matcher = new FnMatcher(matchConfig, config);

		List<BindColumn> columns = matcher.match(headers, exclusiveColumns);
		if (!property.isShared()) {
			for (BindColumn column : columns) {
				exclusiveColumns.add(column.getIndex());
			}
		}
		return columns;

	}

	/**
	 * 创建非嵌套类型的BindMapping
	 *
	 * @param property 字段属性的封装
	 * @param columns  匹配到的列
	 * @param config   LocalConfig
	 * @return FlagMapping
	 */
	private static FlatMapper createFlatMapper(BindProperty property, List<BindColumn> columns, LocalConfig config) {
		if (property.isLineNum()) {
			return new LineNumMapper(columns);
		}
		Collection<ValueHandler> valueHandlers = config.getContentValueHandlers();
		valueHandlers.addAll(property.getValueHandlers());

		FlatMapper mapper;
		JavaType type = property.getType();
		if (property.getType().isMapLikeType()) {
			mapper = createMapMapper(property, columns, valueHandlers);
		} else if (property.getType().isCollectionLikeType()) {
			mapper = createCollectionMapper(property, columns, valueHandlers);
		} else {
			if (columns.isEmpty()) {
				return null;
			}
			Optional<PrimitiveMapper> primitiveMapping = cratePrimitiveMapper(type, columns, valueHandlers);
			if (primitiveMapping.isPresent()) {
				mapper = primitiveMapping.get();
			} else {
				log.error("-> property is [{}] ,type is [{}] , 匹配到多列 index {}", property.getPropertyName(), type, columns);
				throw new SettingException(String.format("property is %s ,type is %s , 匹配到多列", property.getPropertyName(), type));
			}
		}
		return mapper;

	}

	private static AbstractContainerMapper createMapMapper(BindProperty property, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		JavaType type = property.getType();
		Class<?> rawClass = type.getKeyType().getRawClass();
		JavaType contentType = type.getContentType();
		AbstractContainerMapper mapper;
		if (Objects.equals(rawClass, Integer.class)) {
			mapper = new MapIndexKeyMapper(property.getFullName(), contentType, columns, valueHandlers);
		} else if (Objects.equals(rawClass, String.class)) {
			mapper = new MapTextKeyMapper(property.getFullName(), contentType, columns, valueHandlers);
		} else if (Objects.equals(rawClass, Sequence.class)) {
			mapper = new MapSequenceKeyMapper(property.getFullName(), contentType, columns, valueHandlers);
		} else {
			throw new SettingException("Map类型的key只支持 %s(BindColumn.index) | %s (BindColumn.text ) | %s (BindColumn.sequence)", Integer.class, String.class, Sequence.class);
		}
		return mapper;
	}

	private static AbstractContainerMapper createCollectionMapper(BindProperty property, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		JavaType type = property.getType();
		return new CollectionMapper(property.getFullName(), type.getContentType(), columns, valueHandlers);
	}

	private static Optional<PrimitiveMapper> cratePrimitiveMapper(JavaType type, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		if (columns.size() == 1) {
			PrimitiveMapper mapper = new PrimitiveMapper(type, columns.get(0), valueHandlers);
			return Optional.of(mapper);
		} else {
			return Optional.empty();
		}
	}


}
