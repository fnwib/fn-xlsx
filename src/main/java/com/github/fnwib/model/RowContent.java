package com.github.fnwib.model;

import lombok.Getter;

import java.util.List;

/**
 * EXCEL 内容
 */
@Getter
public class RowContent {

	private List<Content> row;

	public RowContent(List<Content> row) {
		this.row = row;
	}
}
