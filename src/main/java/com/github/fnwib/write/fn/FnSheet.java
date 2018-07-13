package com.github.fnwib.write.fn;

import com.github.fnwib.write.model.ExcelContent;
import com.github.fnwib.write.model.RowExcelContent;

import java.util.List;
import java.util.Map;

public interface FnSheet {

	int getStartRow();

	void flush();

	int canWriteSize();

	void addRow(List<ExcelContent> row);

	void addRow(RowExcelContent row);

	void addMergeRow(List<RowExcelContent> rows, List<Integer> mergedRangeIndex);
}
