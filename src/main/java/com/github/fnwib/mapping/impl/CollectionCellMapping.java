package com.github.fnwib.mapping.impl;

import com.github.fnwib.mapping.BindColumn;
import com.github.fnwib.mapping.impl.cell.CellMapping;
import com.github.fnwib.mapping.impl.cell.RawCellMapping;
import com.google.common.collect.Lists;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;

public class CollectionCellMapping implements BindMapping {

	private List<RawCellMapping> mappings;

	public CollectionCellMapping(List<BindColumn> columns) {
		List<RawCellMapping> mappings = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			mappings.add(new RawCellMapping(column.getIndex()));
		}
		this.mappings = mappings;
	}

	@Override
	public List<CellMapping> getCellMappings() {
		List<CellMapping> cs = Lists.newArrayListWithCapacity(mappings.size());
		for (RawCellMapping mapping : mappings) {
			cs.add(mapping);
		}
		return cs;
	}

	@Override
	public Optional<List<Cell>> getValue(Row row) {
		if (mappings.isEmpty()) {
			return Optional.empty();
		}
		List<Cell> result = Lists.newArrayListWithCapacity(mappings.size());
		for (RawCellMapping mapping : mappings) {
			Optional<Cell> value = mapping.getValue(row);
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
