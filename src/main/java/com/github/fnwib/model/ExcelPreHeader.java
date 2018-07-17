package com.github.fnwib.model;

import com.github.fnwib.write.fn.FnCellStyle;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

/**
 * EXCEL 头上面的特殊值
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ExcelPreHeader {
	private int rowNum;
	private int columnIndex;
	private String value;
	private FnCellStyle cellStyle;
	private short height;
}
