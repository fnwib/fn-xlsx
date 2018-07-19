package com.github.fnwib.mapper.flat;

import com.github.fnwib.mapper.cell.RawCellMapping;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.model.Content;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.*;
import java.util.stream.Collectors;

/**
 * List<Cell>的实现
 */
public class CollectionCellMapper extends AbstractContainerMapper {

	private RawCellMapping mapping;

	public CollectionCellMapper(String name, List<BindColumn> columns) {
		super(name, columns);
		this.mapping = new RawCellMapping();
	}

	@Override
	public Optional<List<Cell>> getValue(Row row) {
		if (columns.isEmpty()) {
			return Optional.empty();
		}
		List<Cell> result = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Optional<Cell> value = mapping.getValue(column.getIndex(), row);
			result.add(value.orElse(null));
		}
		return Optional.of(result);
	}

	@Override
	@SuppressWarnings("unchecked")
	public List<Content> getContents(Object value) {
		List<Cell> cells = Objects.nonNull(value) ? (List<Cell>) value : Collections.emptyList();
		check(cells.size());
		Map<Integer, Cell> values = cells.stream().filter(Objects::nonNull).collect(Collectors.toMap(Cell::getColumnIndex, c -> c));
		List<Content> contents = Lists.newArrayListWithCapacity(columns.size());
		for (BindColumn column : columns) {
			Integer index = column.getIndex();
			Cell cell = values.get(index);
			Content content = new Content(cell);
			contents.add(content);
		}
		return contents;
	}

}
