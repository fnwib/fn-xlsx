package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

/**
 * 默认的是三个实现
 */
public enum FnCellStyleType {
	/**
	 * header之前的样式
	 */
	PRR_HEADER(new DefaultPreHeaderCellStyleImpl()),
	/**
	 * header的样式
	 */
	HEADER(new DefaultHeaderCellStyleImpl()),
	/**
	 * header之后内容的样式
	 */
	CONTENT(new DefaultContentCellStyleImpl());

	private FnCellStyle style;

	FnCellStyleType(FnCellStyle cellStyle) {
		this.style = cellStyle;
	}

	public FnCellStyle getStyle() {
		return style;
	}


	public static FnCellStyle toFnCellStyle(CellStyle val) {
		XSSFCellStyle fromCellStyle = (XSSFCellStyle) val;
		XSSFFont fromCellStyleFont = fromCellStyle.getFont();
		return (workbook -> {
			FnCellStyle fnCellStyle = FnCellStyleType.CONTENT.getStyle();
			XSSFCellStyle toCellStyle = fnCellStyle.createCellStyle(workbook);

			toCellStyle.setDataFormat(fromCellStyle.getDataFormat());
			Font font = workbook.createFont();
			font.setFontName(fromCellStyleFont.getFontName());
			font.setFontHeightInPoints(fromCellStyleFont.getFontHeightInPoints());
			font.setFontHeight(fromCellStyleFont.getFontHeight());
			font.setColor(fromCellStyleFont.getColor());
			toCellStyle.setFont(font);

			XSSFColor color1 = fromCellStyle.getFillBackgroundColorColor();
			XSSFColor color2 = fromCellStyle.getFillForegroundColorColor();
			FillPatternType patternEnum = fromCellStyle.getFillPatternEnum();
			toCellStyle.setFillBackgroundColor(color1);
			toCellStyle.setFillForegroundColor(color2);
			toCellStyle.setFillPattern(patternEnum);
			return toCellStyle;
		});
	}
}