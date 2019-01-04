package com.github.fnwib.write;

import com.github.fnwib.model.RowContent;
import com.github.fnwib.model.SheetConfig;

import java.util.List;

public class RowContentWriter extends AbstractWriter<RowContent> {


	public RowContentWriter(SheetConfig sheetConfig) {
		super(sheetConfig);
	}

	@Override
	public void write(RowContent content) {
		check(1);
		fnSheet.addRow(content);
	}

	@Override
	public void write(List<RowContent> contents) {
		for (RowContent content : contents) {
			this.write(content);
		}
	}

	@Override
	public void writeMergedRegion(List<RowContent> rows, List<Integer> mergeIndexes) {
		if (rows.size() == 1) {
			this.write(rows.get(0));
		} else if (rows.size() > 1) {
			check(rows.size());
			fnSheet.addMergeRow(rows, mergeIndexes);
		}
	}
}
