package com.github.fnwib.model;

import lombok.Getter;
import org.apache.poi.ss.usermodel.Cell;

import java.util.Collections;
import java.util.List;

/**
 * EXCEL 内容
 */
@Getter
public class RowContent {
	private List<Cell> cells;
	private List<Content> contents;

	public RowContent(List<Content> row) {
		this(Collections.emptyList(), row);
	}

	public RowContent(List<Cell> cells, List<Content> contents) {
		this.cells = cells;
		this.contents = contents;
	}

	public boolean isEmpty() {
		return cells.isEmpty() && contents.isEmpty();
	}
}
