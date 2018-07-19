package com.github.fnwib.model;

import com.github.fnwib.write.fn.FnCellStyle;
import lombok.*;

/**
 * Excel 头信息
 * 根据id 与 cellIndex 映射
 */
@ToString
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
public class Header {
	private String id;
	private int columnIndex;
	private String value;
	private FnCellStyle cellStyle;
	private short height;
	private int width;
}
