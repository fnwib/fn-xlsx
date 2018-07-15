package com.github.fnwib.mapping.flat;

import com.github.fnwib.mapping.cell.RawCellMapping;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;
import java.util.stream.Collectors;

/**
 * List<Cell>的实现
 */
public class CollectionCellMapping implements FlatMapping {

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
	public List<ExcelContent> getContents(Object value) {
		List<Cell> cells = Objects.nonNull(value) ? (List<Cell>) value : Collections.emptyList();
		Map<Integer, Cell> values = cells.stream().filter(Objects::nonNull).collect(Collectors.toMap(Cell::getColumnIndex, c -> c));
		List<ExcelContent> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Integer index = column.getIndex();
			Cell cell = values.get(index);
			ExcelContent excelContent = new ExcelContent(cell);
			contents.add(excelContent);
		}
		return contents;
//		for (Cell cell : cells) {
//			Cell newCell = null;
//			CellType cellType = cell.getCellTypeEnum();
//			switch (cellType) {
//				case _NONE:
//				case BLANK:
//					break;
//				case ERROR:
//					newCell.setCellType(CellType.ERROR);
//					newCell.setCellValue(cell.getStringCellValue());
//					break;
//				case STRING:
//					newCell.setCellType(CellType.STRING);
//					newCell.setCellValue(cell.getStringCellValue());
//					break;
//				case BOOLEAN:
//					newCell.setCellType(CellType.BOOLEAN);
//					newCell.setCellValue(cell.getStringCellValue());
//					break;
//				case FORMULA:
//					newCell.setCellType(CellType.FORMULA);
//					newCell.setCellValue(cell.getStringCellValue());
//					break;
//				case NUMERIC:
//					newCell.setCellType(CellType.FORMULA);
//					newCell.setCellValue(cell.getNumericCellValue());
//					break;
//				default:
//					break;
//			}
//		}
	}

}
