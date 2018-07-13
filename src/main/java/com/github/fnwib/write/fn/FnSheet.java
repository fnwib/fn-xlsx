package com.github.fnwib.write.fn;

import java.util.List;
import java.util.Map;

public interface FnSheet {

	int getStartRow();

	void flush();

	int canWriteSize();

	void addRow(Map<String, String> row);

	void addMergeRow(List<Map<String, String>> rows, List<Integer> mergedRangeIndex);
}
