package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.DefaultIndexedColorMap;
import org.apache.poi.xssf.usermodel.IndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;

import java.awt.*;

public class DefaultHeaderCellStyleImpl implements FnCellStyle {

	private IndexedColorMap colorMap = new DefaultIndexedColorMap();

	@Override
	public XSSFCellStyle createCellStyle(Workbook workbook) {
		FnCellStyle fnCellStyle = FnCellStyleType.CONTENT.getStyle();
		XSSFCellStyle cellStyle = fnCellStyle.createCellStyle(workbook);
		Font font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints(((short) 12));
		cellStyle.setFont(font);
		Color color = new Color(254, 230, 153);
		XSSFColor myColor = new XSSFColor(color, colorMap);
		cellStyle.setFillForegroundColor(myColor);
		cellStyle.setFillBackgroundColor(myColor);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return cellStyle;
	}

}
