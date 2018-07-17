package com.github.fnwib.mapper.cell;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.testentity.TestEnumType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.Optional;

public class DefaultHandlerTest {

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0);
			row.createCell(1).setCellValue(TestEnumType.A.name());
			check("null", 0, row, Optional.empty());
			check("test1", 1, row, Optional.of(TestEnumType.A.name()));
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, int column, Row row, Optional expected) {
		DefaultHandler handler = new DefaultHandler();
		Optional<String> value = handler.getValue(column, row);
		Assert.assertEquals(message, expected, value);
	}
}