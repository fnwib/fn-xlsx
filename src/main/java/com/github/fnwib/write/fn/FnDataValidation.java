package com.github.fnwib.write.fn;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;

public final class FnDataValidation {

	private String[] pos;

	public FnDataValidation(String[] pos) {
		this.pos = pos;
	}

	public void createDataValidation(Cell cell, Sheet sheet, int maxRowNum) {
		int rowIndex = cell.getRowIndex();
		int columnIndex = cell.getColumnIndex();
		DataValidationHelper helper = sheet.getDataValidationHelper();
		CellRangeAddressList addressList = new CellRangeAddressList(rowIndex, maxRowNum, columnIndex, columnIndex);
		//设置下拉框数据
		DataValidationConstraint constraint = helper.createExplicitListConstraint(pos);
		DataValidation dataValidation = helper.createValidation(constraint, addressList);
		//处理Excel兼容性问题
		dataValidation.setSuppressDropDownArrow(true);
		if (dataValidation instanceof XSSFDataValidation) {
			dataValidation.setShowErrorBox(true);
		}
		sheet.addValidationData(dataValidation);
	}
}
