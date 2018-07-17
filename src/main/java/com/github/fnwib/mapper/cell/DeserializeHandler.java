package com.github.fnwib.mapper.cell;

import com.github.fnwib.databing.deser.CellDeserializer;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

/**
 * 注册的CellDeserializer的实现
 */
public class DeserializeHandler implements CellValueHandler {

	private final CellDeserializer<?> deserializer;

	public DeserializeHandler(CellDeserializer<?> deserializer) {
		this.deserializer = deserializer;
	}

	@Override
	public Optional<?> getValue(int indexColumn, Row row) {
		Cell cell = row.getCell(indexColumn);
		Object deserialize = deserializer.deserialize(cell);
		return Optional.ofNullable(deserialize);
	}
}
