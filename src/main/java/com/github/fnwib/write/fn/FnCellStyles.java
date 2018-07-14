package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

public class FnCellStyles {

	public static FnCellStyle getOrDefault(FnCellStyle fnCellStyle, FnCellStyleType type) {
		if (fnCellStyle == null) {
			return type.getStyle();
		}
		return fnCellStyle;
	}


	public static FnCellStyle to(XSSFCellStyle fromCellStyle) {
		XSSFFont fromCellStyleFont = fromCellStyle.getFont();
		return (workbook -> {
			FnCellStyle fnCellStyle = FnCellStyleType.CONTENT.getStyle();
			XSSFCellStyle toCellStyle = fnCellStyle.createCellStyle(workbook);
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
