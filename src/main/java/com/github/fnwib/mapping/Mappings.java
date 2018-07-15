package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.flat.*;
import com.github.fnwib.mapping.cell.AbstractCellStringMapping;
import com.github.fnwib.mapping.cell.NumberMapping;
import com.github.fnwib.mapping.cell.SimpleMapping;
import com.github.fnwib.mapping.cell.StringMapping;
import com.github.fnwib.mapping.flat.LineNumMapping;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.model.BindProperty;
import com.github.fnwib.mapping.model.MatchConfig;
import com.github.fnwib.mapping.nested.NestedMapping;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.model.ExcelHeader;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;

import java.util.*;
import java.util.concurrent.atomic.LongAdder;
import java.util.stream.Collectors;

@Slf4j
public class Mappings {

	private Mappings() {
	}

	public static <T> NestedMapping<T> createNestedMapping(Class<T> type, LocalConfig config, List<ExcelHeader> headers) {
		LongAdder level = new LongAdder();
		NestedMapping<T> nestedMapping = createNestedMapping(type, config, headers, Sets.newHashSet(), level);
		if (level.intValue() >= 3) {
			throw new SettingException("'%s'嵌套层数超过两层", type);
		}
		return nestedMapping;
	}

	/**
	 * @param type             嵌套类型
	 * @param config           LocalConfig
	 * @param headers          Headers(等价于org.apache.poi.ss.usermodel.Row)
	 * @param exclusiveColumns 独占模式的列
	 * @param level            嵌套类型的层级
	 * @param <T>              嵌套类型
	 * @return NestedMapping
	 */
	private static <T> NestedMapping<T> createNestedMapping(Class<T> type, LocalConfig config, List<ExcelHeader> headers, Set<Integer> exclusiveColumns, LongAdder level) {
		JavaType javaType = TypeFactory.defaultInstance().constructType(type);
		if (type.isPrimitive()) {
			throw new SettingException("不支持这样的嵌套类型");
		}
		List<BindProperty> properties = BeanResolver.INSTANCE.getProperties(type).stream().map(Property::toBindParam)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.sorted(Comparator.comparing(BindProperty::getOrder))
				.collect(Collectors.toList());
		for (BindProperty property : properties) {
			if (property.isNested()) {
				level.increment();
				NestedMapping<?> nestedMapping = createNestedMapping(property.getRawClass(), config, headers, exclusiveColumns, level);
				property.setBindMapping(nestedMapping);
				continue;
			}
			List<BindColumn> columns = match(property, config, headers, exclusiveColumns);
			BindMapping mapping = createFlatMapping(property, columns, config);
			property.setBindMapping(mapping);
		}
		return new NestedMapping<>(javaType, properties);
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
	private static List<BindColumn> match(BindProperty property, LocalConfig config, List<ExcelHeader> headers, Set<Integer> exclusiveColumns) {
		MatchConfig matchConfig = property.getMatchConfig();
		FnMatcher matcher = new FnMatcher(matchConfig, config);
		if (property.isExclusive()) {
			return matcher.match(headers, exclusiveColumns);
		}
		return matcher.match(headers, Collections.emptySet());
	}

	/**
	 * 创建非嵌套类型的BindMapping
	 *
	 * @param property 字段属性的封装
	 * @param columns  匹配到的列
	 * @param config   LocalConfig
	 * @return FlagMapping
	 */
	private static BindMapping createFlatMapping(BindProperty property, List<BindColumn> columns, LocalConfig config) {
		if (property.isLineNum()) {
			return new LineNumMapping(columns);
		}
		if (columns.isEmpty()) {
			return null;
		}
		BindMapping mapping;
		Collection<ValueHandler> valueHandlers = config.getContentValueHandlers();
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
		return mapping;

	}

	public static AbstractCellStringMapping createSimpleMapping(JavaType type, Collection<ValueHandler> valueHandlers) {
		AbstractCellStringMapping mapping;
		Class<?> rawClass = type.getRawClass();
		if (String.class == rawClass) {
			mapping = new StringMapping(valueHandlers);
		} else if (Number.class.isAssignableFrom(rawClass)) {
			mapping = new NumberMapping();
		} else {
			mapping = new SimpleMapping(type);
		}
		return mapping;
	}

	private static AbstractMapMapping createMapMapping(JavaType type, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		JavaType keyType = type.getKeyType();
		JavaType contentType = type.getContentType();
		AbstractMapMapping mapping;
		Class<?> rawClass = keyType.getRawClass();
		if (Objects.equals(rawClass, Integer.class)) {
			mapping = new MapIndexKeyMapping(contentType, columns, valueHandlers);
		} else if (Objects.equals(rawClass, String.class)) {
			mapping = new MapTextKeyMapping(contentType, columns, valueHandlers);
		} else if (Objects.equals(rawClass, Sequence.class)) {
			mapping = new MapSequenceKeyMapping(contentType, columns, valueHandlers);
		} else {
			String format = String.format("Map类型的key只支持 %s(cell index) | %s (cell text ) | %s (cell sequence)", Integer.class, String.class, Sequence.class);
			throw new SettingException(format);
		}
		return mapping;
	}

	private static BindMapping createCollectionMapping(JavaType type, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		BindMapping mapping;
		if (Cell.class.isAssignableFrom(type.getContentType().getRawClass())) {
			if (type.getRawClass() != List.class) {
				throw new SettingException("只支持List<Cell>", Cell.class);
			}
			mapping = new CollectionCellMapping(columns);
		} else {
			mapping = new CollectionMapping(type.getContentType(), columns, valueHandlers);
		}
		return mapping;
	}

	private static Optional<PrimitiveMapping> cratePrimitiveMapping(JavaType type, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		PrimitiveMapping mapping;
		if (columns.size() == 1) {
			mapping = new PrimitiveMapping(type, columns.get(0), valueHandlers);
			return Optional.of(mapping);
		} else {
			return Optional.empty();
		}
	}


}
