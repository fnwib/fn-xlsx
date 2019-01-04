package com.github.fnwib.model;

import org.apache.poi.ss.usermodel.Cell;

import java.util.Collections;
import java.util.List;

/**
 * EXCEL 内容
 * <p>
 * 优先写cells
 * 也就是说如果columnIndex重复contents的内容会覆盖cell的内容
 * <p>
 * 因此在创建该对象时候做一下检查
 */
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

	public List<Cell> getCells() {
		return cells;
	}

	public List<Content> getContents() {
		return contents;
	}
}
