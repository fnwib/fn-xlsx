package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.impl.*;
import com.github.fnwib.mapping.impl.cell.AbstractCellStringMapping;
import com.github.fnwib.mapping.impl.cell.NumberMapping;
import com.github.fnwib.mapping.impl.cell.SimpleMapping;
import com.github.fnwib.mapping.impl.cell.StringMapping;
import com.github.fnwib.mapping.model.BindColumn;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class Mappings {

	private Mappings() {
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

	public static AbstractMapMapping createMapMapping(JavaType type, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
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

	public static BindMapping createCollectionMapping(JavaType type, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
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

	public static Optional<PrimitiveMapping> cratePrimitiveMapping(JavaType type, List<BindColumn> columns, Collection<ValueHandler> valueHandlers) {
		PrimitiveMapping mapping;
		if (columns.size() == 1) {
			mapping = new PrimitiveMapping(type, columns.get(0), valueHandlers);
			return Optional.of(mapping);
		} else {
			return Optional.empty();
		}
	}

}
