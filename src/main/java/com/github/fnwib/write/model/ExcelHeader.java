package com.github.fnwib.write.model;

import com.github.fnwib.write.fn.FnCellStyle;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Excel 头信息
 * 根据id 与 cellIndex 映射
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ExcelHeader {
	private String id;
	private int cellIndex;
	private String value;
	private FnCellStyle cellStyle;
	private short height;
	private int width;
}
