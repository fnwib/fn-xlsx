package com.github.fnwib.mapping.impl.cell;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.deser.CellDeserializer;
import com.github.fnwib.databing.ser.Serializer;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.util.ExcelUtil;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public class SimpleMapping implements CellMapping {

	private final CellDeserializer<?> deserializer;
	private final Serializer serializer;
	private final Integer bindColumn;
	private final CellText EMPTY;


	public SimpleMapping(JavaType contentType, Integer bindColumn) {
		this.bindColumn = bindColumn;
		this.deserializer = Context.INSTANCE.findCellDeserializer(contentType);
		this.serializer = Context.INSTANCE.findSerializer(contentType);
		this.EMPTY = new CellText(bindColumn, StringUtils.EMPTY);
	}

	@Override
	public Integer getColumn() {
		return bindColumn;
	}

	@Override
	public Optional<String> getValue(Row row) {
		Cell cell = row.getCell(bindColumn);
		if (cell == null) {
			return Optional.empty();
		}
		if (deserializer != null) {
			Object deserialize = deserializer.deserialize(cell);
			if (deserialize == null) {
				return Optional.empty();
			} else {
				return Optional.of(deserialize.toString());
			}
		}
		switch (cell.getCellTypeEnum()) {
			case BLANK:
				return Optional.empty();
			case NUMERIC:
				return Optional.of(cell.getStringCellValue());
			case STRING:
				return ValueUtil.getCellValue(cell);
			case ERROR:
			case BOOLEAN:
			case FORMULA:
			case _NONE:
				String format = String.format("坐标[%s][%s]值为[%s],类型是[%s]",
						row.getRowNum() + 1,
						ExcelUtil.num2Column(cell.getColumnIndex() + 1),
						cell.getStringCellValue(),
						cell.getCellTypeEnum().name());
				throw new ExcelException(format);
			default:
				throw new NotSupportedException(" [" + cell.getStringCellValue() + "] unknown type");
		}

	}

	@Override
	public Optional<CellText> createCellText(Object value) {
		if (value == null) {
			return Optional.of(EMPTY);
		}
		CellText cellText = new CellText(bindColumn, value.toString());
		return Optional.of(cellText);
	}
}
