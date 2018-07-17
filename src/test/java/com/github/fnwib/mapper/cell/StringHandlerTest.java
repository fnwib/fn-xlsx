package com.github.fnwib.mapper.cell;

import com.github.fnwib.exception.ExcelException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;

public class StringHandlerTest {

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0);
			row.createCell(1).setCellValue("test1");
			check("null", 0, row, Optional.empty());
			check("test1", 1, row, Optional.of("test1"));
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	/**
	 * if throws ErrorCellType.CELL_NUMERIC_TO_STRING.get(cell)
	 */
	@Test(expected = ExcelException.class)
	public void number() throws IOException {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(3).setCellValue(0.000);
			StringHandler handler = new StringHandler(Collections.emptyList());
			handler.getValue(3, row);
		}
	}
	
	private void check(String message, int column, Row row, Optional expected) {
		StringHandler handler = new StringHandler(Collections.emptyList());
		Optional<String> value = handler.getValue(column, row);
		Assert.assertEquals(message, expected, value);
	}
}