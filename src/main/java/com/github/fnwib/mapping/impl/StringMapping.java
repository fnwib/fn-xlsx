package com.github.fnwib.mapping.impl;

import com.github.fnwib.databing.convert.impl.BeanConverter;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.mapping.BindMapping;
import com.github.fnwib.util.ExcelUtil;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.formula.functions.T;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Objects;
import java.util.Optional;

public class StringMapping implements BindMapping {

	private static final Logger log = LoggerFactory.getLogger(BeanConverter.class);

	private final Collection<ValueHandler> valueHandlers;
	private final Integer bindColumn;
	private final CellText EMPTY;


	public StringMapping(Integer bindColumn, Collection<ValueHandler> valueHandlers) {
		this.valueHandlers = valueHandlers;
		this.bindColumn = bindColumn;
		this.EMPTY = new CellText(bindColumn, StringUtils.EMPTY);
	}

	@Override
	public Optional<String> getValue(Row row) {
		Cell cell = row.getCell(bindColumn);
		if (cell == null) {
			return Optional.empty();
		}
		switch (cell.getCellTypeEnum()) {
			case BLANK:
				return Optional.empty();
			case NUMERIC:
			case STRING:
				return ValueUtil.getCellValue(cell, valueHandlers);
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
