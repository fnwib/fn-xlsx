package com.github.fnwib.write.model;

import com.github.fnwib.write.fn.FnCellStyle;
import lombok.*;
import org.apache.poi.ss.usermodel.CellStyle;

/**
 * Excel 头信息
 * 根据id 与 cellIndex 映射
 */
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class ExcelHeader {
	private String id;
	private int columnIndex;
	private String value;
	private FnCellStyle cellStyle;
	private short height;
	private int width;
}
