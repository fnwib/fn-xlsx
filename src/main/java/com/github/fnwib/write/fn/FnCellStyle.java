package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

/**
 *
 */
@FunctionalInterface
public interface FnCellStyle {

	XSSFCellStyle createCellStyle(Workbook workbook);

}
