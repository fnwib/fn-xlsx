package com.github.fnwib.databing;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.List;

/**
 * 替换为 com.github.fnwib.write.fn.FnSheet
 *
 * @param <T>
 */
@Deprecated
public interface LineWriter<T> {

	void setSheet(Sheet sheet);

	void setCellStyle(CellStyle defaultCellStyle);

	void convert(int rowNum, T element);

	void convert(int rowNum, List<T> elements, List<Integer> mergedRangeIndexes);

}
