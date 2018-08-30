package com.github.fnwib.write.fn;

import com.github.fnwib.exception.NotSupportedException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFFont;

import java.util.HashMap;
import java.util.Map;

public class FnCellStyleFactory {
	private Workbook workbook;
	private Map<CellStyle, CellStyle> cellStyleMap;

	public FnCellStyleFactory(Workbook workbook) {
		this.workbook = workbook;
		cellStyleMap = new HashMap<>();
	}


	public CellStyle getOrDefault(FnCellStyle fnCellStyle, FnCellStyleType type) {
		if (fnCellStyle == null) {
			return type.getStyle().createCellStyle(workbook);
		}
		return fnCellStyle.createCellStyle(workbook);
	}

	public CellStyle copyCellStyle(CellStyle val) {
		if (XSSFCellStyle.class != val.getClass()) {
			throw new NotSupportedException("不支持XSSFCellStyle以外的CellStyle");
		}
		if (cellStyleMap.containsKey(val)) {
			return cellStyleMap.get(val);
		}
		XSSFCellStyle cellStyle = toFnCellStyle(val).createCellStyle(workbook);
		cellStyleMap.put(val, cellStyle);
		return cellStyle;
	}

	private FnCellStyle toFnCellStyle(CellStyle val) {
		XSSFCellStyle fromCellStyle = (XSSFCellStyle) val;
		XSSFFont fromCellStyleFont = fromCellStyle.getFont();
		return (workbook -> {
			FnCellStyle fnCellStyle = FnCellStyleType.CONTENT.getStyle();
			XSSFCellStyle toCellStyle = fnCellStyle.createCellStyle(workbook);

			toCellStyle.setDataFormat(fromCellStyle.getDataFormat());
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
