package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.awt.Color;

public class DefaultHeaderCellStyleImpl implements FnCellStyle {

	@Override
	public CellStyle createCellStyle(Workbook workbook) {
		FnCellStyle fnCellStyle = FnCellStyleType.CONTENT.getStyle();
		XSSFCellStyle cellStyle = (XSSFCellStyle) fnCellStyle.createCellStyle(workbook);
		Font font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints(((short) 12));
		cellStyle.setFont(font);
		Color color = new Color(254, 230, 153);
		XSSFColor myColor = new XSSFColor(color);
		cellStyle.setFillForegroundColor(myColor);
		cellStyle.setFillBackgroundColor(myColor);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return cellStyle;
	}

}
