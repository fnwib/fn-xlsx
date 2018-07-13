package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.CustomIndexedColorMap;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColors;

public class DefaultPreHeaderCellStyleImpl implements FnCellStyle {
	@Override
	public CellStyle createCellStyle(Workbook workbook) {
		FnCellStyle fnCellStyle = FnCellStyleType.CONTENT.getStyle();
		XSSFCellStyle cellStyle = (XSSFCellStyle) fnCellStyle.createCellStyle(workbook);

		Font font = workbook.createFont();
		font.setFontName("华文行楷");
		font.setFontHeightInPoints(((short) 18));
		cellStyle.setFont(font);
		return cellStyle;
	}
}
