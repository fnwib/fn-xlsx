package com.github.fnwib.write.fn;

import com.github.fnwib.exception.NotSupportedException;
import org.apache.poi.ss.usermodel.CellStyle;
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

	/**
	 * 只支持XSSFCellStyle
	 * <p>
	 * 如果传进来的不是
	 *
	 * @param val
	 * @return
	 */
	public static FnCellStyle toXSSFCellStyle(CellStyle val) {
		if (XSSFCellStyle.class != val.getClass()) {
			throw new NotSupportedException("不支持XSSFCellStyle以外的CellStyle");
		}
		XSSFCellStyle fromCellStyle = (XSSFCellStyle) val;
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
