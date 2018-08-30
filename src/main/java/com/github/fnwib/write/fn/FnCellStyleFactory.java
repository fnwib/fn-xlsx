package com.github.fnwib.write.fn;

import com.github.fnwib.exception.NotSupportedException;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;

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
		XSSFCellStyle cellStyle = FnCellStyleType.toFnCellStyle(val).createCellStyle(workbook);
		cellStyleMap.put(val, cellStyle);
		return cellStyle;
	}

}
