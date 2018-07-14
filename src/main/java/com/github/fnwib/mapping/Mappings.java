package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.impl.*;
import com.github.fnwib.mapping.impl.cell.AbstractCellStringMapping;
import com.github.fnwib.mapping.impl.cell.NumberMapping;
import com.github.fnwib.mapping.impl.cell.SimpleMapping;
import com.github.fnwib.mapping.impl.cell.StringMapping;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.model.BindProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
public class Mappings {

	private Mappings() {
	}

	public static void createMapping(BindProperty bindProperty, LocalConfig config) {
		if (bindProperty.isNested()) {
			if (bindProperty.getType().isPrimitive()) {
				throw new SettingException("不支持这样的嵌套类型");
			}
			for (BindProperty property : bindProperty.getSubBindProperties()) {
				createFlatMapping(property, config);
			}
		} else {
			createFlatMapping(bindProperty, config);
		}
	}

	private static void createFlatMapping(BindProperty property, LocalConfig config) {
		if (property.isLineNum()) {
			BindMapping mapping = new LineNumMapping(property.getBindColumns());
			property.setBindMapping(mapping);
		}
		List<BindColumn> columns = property.getBindColumns();
		if (columns.isEmpty()) {
			return;
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
		property.setBindMapping(mapping);

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
