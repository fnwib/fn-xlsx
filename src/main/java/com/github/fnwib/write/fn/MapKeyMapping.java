package com.github.fnwib.write.fn;

import com.github.fnwib.model.ExcelHeader;

@FunctionalInterface
public interface MapKeyMapping {

	public int getIndex(String value, ExcelHeader excelHeader);
}
