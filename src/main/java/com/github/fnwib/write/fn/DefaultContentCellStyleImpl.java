package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

public class DefaultContentCellStyleImpl implements FnCellStyle {
	@Override
	public XSSFCellStyle createCellStyle(Workbook workbook) {
		XSSFCellStyle cellStyle = (XSSFCellStyle) workbook.createCellStyle();
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setAlignment(HorizontalAlignment.CENTER);
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("text"));
		return cellStyle;
	}
}
