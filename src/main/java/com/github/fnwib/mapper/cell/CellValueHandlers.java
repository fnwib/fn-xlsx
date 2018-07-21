package com.github.fnwib.mapper.cell;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.context.Context;
import com.github.fnwib.plugin.ValueHandler;
import com.github.fnwib.plugin.deser.CellDeserializer;

import java.util.Collection;

public class CellValueHandlers {

	public static CellValueHandler createCellValueHandler(JavaType type, Collection<ValueHandler> valueHandlers) {
		CellDeserializer<?> deserializer = Context.INSTANCE.findCellDeserializer(type);
		if (deserializer != null) {
			return new DeserializeHandler(deserializer);
		}
		return new DefaultHandler(valueHandlers);
	}

}
