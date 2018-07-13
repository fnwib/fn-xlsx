package com.github.fnwib.write.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * EXCEL 头上面的特殊值
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ExcelPreHeader {
	private int rowNum;
	private int cellIndex;
	private String value;
	private CellStyle cellStyle;
}
