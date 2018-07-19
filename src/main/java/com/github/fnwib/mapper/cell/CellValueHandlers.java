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
		Class<?> rawClass = type.getRawClass();
		if (String.class == rawClass) {
			return new StringHandler(valueHandlers);
		} else if (isNumber(rawClass)) {
			return new NumberHandler();
		} else {
			return new DefaultHandler();
		}
	}

	public static boolean isNumber(Class<?> rawClass) {
		if (Number.class.isAssignableFrom(rawClass)) {
			return true;
		} else if (long.class == rawClass
				|| int.class == rawClass
				|| short.class == rawClass
				|| double.class == rawClass
				|| float.class == rawClass) {
			return true;
		}
		return false;
	}

}