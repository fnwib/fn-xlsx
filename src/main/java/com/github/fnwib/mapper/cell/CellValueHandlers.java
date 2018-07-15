package com.github.fnwib.mapper.cell;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.deser.CellDeserializer;
import com.github.fnwib.databing.valuehandler.ValueHandler;

import java.util.Collection;

public class CellValueHandlers {

	public static CellValueHandler createCellValueHandler(JavaType type, Collection<ValueHandler> valueHandlers) {
		CellDeserializer<?> deserializer = Context.INSTANCE.findCellDeserializer(type);
		if (deserializer != null) {
			return new DeserializeHandler(deserializer);
		}
		return createAbstractCellValueHandler(type, valueHandlers);
	}

	private static AbstractCellValueHandler createAbstractCellValueHandler(JavaType type, Collection<ValueHandler> valueHandlers) {
		Class<?> rawClass = type.getRawClass();
		if (String.class == rawClass) {
			return new StringHandler(valueHandlers);
		} else if (Number.class.isAssignableFrom(rawClass)) {
			return new NumberHandler();
		} else {
			return new SimpleHandler();
		}
	}

}
