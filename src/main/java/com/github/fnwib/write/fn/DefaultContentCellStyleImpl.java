package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.*;

public class DefaultContentCellStyleImpl implements FnCellStyle {
	@Override
	public CellStyle createCellStyle(Workbook workbook) {
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setBorderTop(BorderStyle.THIN);
		cellStyle.setBorderLeft(BorderStyle.THIN);
		cellStyle.setBorderRight(BorderStyle.THIN);
		cellStyle.setBorderBottom(BorderStyle.THIN);
		cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
		cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
		cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("text"));
		return cellStyle;
	}
}
