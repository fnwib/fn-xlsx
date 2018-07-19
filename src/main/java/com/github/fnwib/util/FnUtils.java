package com.github.fnwib.util;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.RowReader;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.PreHeader;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.model.SheetTemplateView;
import com.github.fnwib.write.fn.FnCellStyles;
import com.github.fnwib.write.fn.FnDataValidation;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Slf4j
public class FnUtils {
	/**
	 * 只做匹配使用，不需要样式
	 *
	 * @param row
	 * @return
	 */
	public static List<Header> toHeader(Row row) {
		List<Header> headers = Lists.newArrayListWithCapacity(row.getLastCellNum());
		for (Cell cell : row) {
			Header header = Header.builder()
					.columnIndex(cell.getColumnIndex())
					.value(cell.getStringCellValue())
					.build();
			headers.add(header);
		}
		return headers;
	}


	private static List<Header> toHeaderWithStyle(Row row) {
		List<? extends DataValidation> validations = row.getSheet().getDataValidations();
		List<Header> headers = Lists.newArrayListWithCapacity(row.getLastCellNum());
		for (Cell cell : row) {
			Header header = Header.builder()
					.columnIndex(cell.getColumnIndex())
					.value(cell.getStringCellValue())
					.height(row.getHeight())
					.width(row.getSheet().getColumnWidth(cell.getColumnIndex()))
					.cellStyle(FnCellStyles.toXSSFCellStyle(cell.getCellStyle()))
					.dataValidation(getFnDataValidation(cell.getRowIndex(), cell.getColumnIndex(), validations))
					.build();
			headers.add(header);
		}
		return headers;
	}

	private static FnDataValidation getFnDataValidation(int rowIndex, int columnIndex, List<? extends DataValidation> validations) {

		for (DataValidation validation : validations) {
			CellRangeAddressList regions = validation.getRegions();
			if (null == regions || regions.getSize() == 0) {
				continue;
			}
			for (CellRangeAddress rangeAddress : regions.getCellRangeAddresses()) {
				int y1 = rangeAddress.getFirstRow();
				int y2 = rangeAddress.getLastRow();
				int x1 = rangeAddress.getFirstColumn();
				int x2 = rangeAddress.getLastColumn();
				if (columnIndex >= x1 && columnIndex <= x2 && rowIndex >= y1 && rowIndex <= y2) {
					DataValidationConstraint constraint = validation.getValidationConstraint();
					return new FnDataValidation(constraint.getExplicitListValues());
				}
			}
		}
		return null;
	}

	private static List<PreHeader> toPreHeaderWithStyle(Row row) {
		List<PreHeader> headers = Lists.newArrayListWithCapacity(row.getLastCellNum());
		for (Cell cell : row) {
			PreHeader header = PreHeader.builder()
					.rowNum(row.getRowNum())
					.columnIndex(cell.getColumnIndex())
					.value(cell.getStringCellValue())
					.height(row.getHeight())
					.width(row.getSheet().getColumnWidth(cell.getColumnIndex()))
					.cellStyle(FnCellStyles.toXSSFCellStyle(cell.getCellStyle()))
					.build();
			headers.add(header);
		}
		return headers;
	}

	public static <T> void merge(SheetConfig config, File template, RowReader<T> reader) {
		try {
			if (template == null) {
				return;
			}
			SheetTemplateView view = config.getView();
			XSSFWorkbook workbook = new XSSFWorkbook(template);
			XSSFSheet sheet = workbook.getSheetAt(0);
			for (Row row : sheet) {
				if (reader.isEmpty(row)) {
					continue;
				}
				boolean match = reader.match(row);
				if (match) {
					List<Header> headers = toHeaderWithStyle(row);
					view.prependHeaders(headers);
					return;
				} else {
					List<PreHeader> headers = toPreHeaderWithStyle(row);
					view.prependPreHeader(headers);
				}
			}
			throw new ExcelException("模板错误");
		} catch (IOException | InvalidFormatException e) {
			throw new ExcelException(e);
		}
	}

}
