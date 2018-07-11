package com.github.fnwib.mapping.impl;

import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.mapping.impl.cell.RawCellMapping;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

public class CollectionCellMapping implements BindMapping {

	private RawCellMapping mapping;
	private List<BindColumn> columns;

	public CollectionCellMapping(List<BindColumn> columns) {
		this.mapping = new RawCellMapping();
		this.columns = columns;
	}

	@Override
	public List<BindColumn> getColumns() {
		return columns;
	}

	@Override
	public Optional<List<Cell>> getValue(Row row) {
		if (columns.isEmpty()) {
			return Optional.empty();
		}
		List<Cell> result = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Optional<Cell> value = mapping.getValue(column.getIndex(), row);
			value.ifPresent(v -> result.add(v));
		}
		return Optional.of(result);
	}

	@Override
	public void setValueToRow(Object value, Row row) {
		if (Objects.isNull(value)) {
			return;
		}
		List<Cell> cells = (List<Cell>) value;
		for (Cell cell : cells) {
			int index = cell.getColumnIndex();
			Cell newCell = row.createCell(index);
			CellType cellType = cell.getCellTypeEnum();
			switch (cellType) {
				case _NONE:
				case BLANK:
					break;
				case ERROR:
					newCell.setCellType(CellType.ERROR);
					newCell.setCellValue(cell.getStringCellValue());
					break;
				case STRING:
					newCell.setCellType(CellType.STRING);
					newCell.setCellValue(cell.getStringCellValue());
					break;
				case BOOLEAN:
					newCell.setCellType(CellType.BOOLEAN);
					newCell.setCellValue(cell.getStringCellValue());
					break;
				case FORMULA:
					newCell.setCellType(CellType.FORMULA);
					newCell.setCellValue(cell.getStringCellValue());
					break;
				case NUMERIC:
					newCell.setCellType(CellType.FORMULA);
					newCell.setCellValue(cell.getNumericCellValue());
					break;
				default:
					break;
			}
		}
	}

}
