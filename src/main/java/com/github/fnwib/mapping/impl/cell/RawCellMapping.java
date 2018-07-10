package com.github.fnwib.mapping.impl.cell;

import com.github.fnwib.exception.NotSupportedException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.List;
import java.util.Optional;

public class RawCellMapping implements CellMapping {

	private Integer column;

	public RawCellMapping(Integer column) {
		this.column = column;
	}

	@Override
	public Integer getColumn() {
		return column;
	}

	@Override
	public Optional<Cell> getValue(Row row) {
		return Optional.of(row.getCell(column));
	}

	/**
	 * value 的类型是cell 内部维护了位置
	 *
	 * @param value
	 * @param row
	 */
	@Override
	public void setValueToRow(Object value, Row row) {
		throw new NotSupportedException();
	}
}
