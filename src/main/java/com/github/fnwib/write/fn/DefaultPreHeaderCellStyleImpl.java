package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class DefaultPreHeaderCellStyleImpl implements FnCellStyle {
	@Override
	public XSSFCellStyle createCellStyle(Workbook workbook) {
		FnCellStyle fnCellStyle = FnCellStyleType.CONTENT.getStyle();
		XSSFCellStyle cellStyle =  fnCellStyle.createCellStyle(workbook);

		Font font = workbook.createFont();
		font.setFontName("华文行楷");
		font.setFontHeightInPoints(((short) 18));
		cellStyle.setFont(font);
		return cellStyle;
	}
}
