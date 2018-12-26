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

	private final Color color;

	public DefaultHeaderCellStyleImpl() {
		this.color = new Color(254, 230, 153);
	}

	public DefaultHeaderCellStyleImpl(Color color) {
		this.color = color;
	}

	@Override
	public XSSFCellStyle createCellStyle(Workbook workbook) {
		FnCellStyle fnCellStyle = FnCellStyleType.CONTENT.getStyle();
		XSSFCellStyle cellStyle = fnCellStyle.createCellStyle(workbook);
		Font font = workbook.createFont();
		font.setFontName("宋体");
		font.setFontHeightInPoints(((short) 12));
		cellStyle.setFont(font);
		XSSFColor myColor = new XSSFColor(color, colorMap);
		cellStyle.setFillForegroundColor(myColor);
		cellStyle.setFillBackgroundColor(myColor);
		cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		return cellStyle;
	}

}
