package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

/**
 * 工具类com.github.fnwib.write.fn.FnCellStyles
 * 默认的三个实现 com.github.fnwib.write.fn.FnCellStyleType
 * <p>
 * ExcelPreHeader 默认使用 com.github.fnwib.write.fn.DefaultPreHeaderCellStyleImpl
 * ExcelHeader 默认使用 com.github.fnwib.write.fn.DefaultHeaderCellStyleImpl
 * ExcelContent 默认使用 com.github.fnwib.write.fn.DefaultContentCellStyleImpl
 */
@FunctionalInterface
public interface FnCellStyle {

	XSSFCellStyle createCellStyle(Workbook workbook);

}
