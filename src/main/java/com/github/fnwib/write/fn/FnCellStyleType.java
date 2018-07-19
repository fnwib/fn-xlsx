package com.github.fnwib.write.fn;

/**
 * 默认的是哪个实现
 */
public enum FnCellStyleType {
	PRR_HEADER(new DefaultPreHeaderCellStyleImpl()),
	HEADER(new DefaultHeaderCellStyleImpl()),
	CONTENT(new DefaultContentCellStyleImpl());

	private FnCellStyle style;

	FnCellStyleType(FnCellStyle cellStyle) {
		this.style = cellStyle;
	}

	public FnCellStyle getStyle() {
		return style;
	}
}