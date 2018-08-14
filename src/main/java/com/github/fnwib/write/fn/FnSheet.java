package com.github.fnwib.write.fn;

import com.github.fnwib.model.RowContent;

import java.util.List;

public interface FnSheet {
	/**
	 * 下一次写入数据的位置
	 *
	 * @return
	 */
	int getStartRow();

	/**
	 * 写出Excel 关闭IO
	 */
	void flush();

	/**
	 * 当前sheet还可以写多少行
	 */
	int canWriteSize();

	/**
	 * 写一行数据
	 */
	void addRow(RowContent row);

	/**
	 * 写若干行合并单元格的数据
	 *
	 * @param rows             若干行数据
	 * @param mergedRangeIndex 合并单元格的columns
	 */
	void addMergeRow(List<RowContent> rows, List<Integer> mergedRangeIndex);
}
