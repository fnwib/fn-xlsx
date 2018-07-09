package com.github.fnwib.mapping;

import com.github.fnwib.write.CellText;
import org.apache.poi.ss.usermodel.Row;

import java.util.Optional;

public interface BindMapping {

	/**
	 * poi row to String
	 *
	 * @param row
	 * @return
	 */
	Optional<String> getValue(Row row);

	/**
	 * 默认使用value的toString方法
	 *
	 * @param value
	 * @return
	 */
	Optional<CellText> createCellText(Object value);
}