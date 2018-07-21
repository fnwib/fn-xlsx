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
import java.util.Collections;
import java.util.Optional;

public class DefaultHandlerTest {

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0);
			row.createCell(1).setCellValue(TestEnumType.A.name());
			row.createCell(2).setCellValue("test1");
			row.createCell(3).setCellValue(1.0d);
			row.createCell(4).setCellValue(1d);
			row.createCell(5).setCellValue(1.001d);
			check("null", 0, row, Optional.empty());
			check("test1", 1, row, Optional.of(TestEnumType.A.name()));
			check("test1", 2, row, Optional.of("test1"));

			check("1.0d to 1", 3, row, Optional.of("1"));
			check("1d to  1", 4, row, Optional.of("1"));
			check("1.001 to 1.001", 5, row, Optional.of("1.001"));

		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, int column, Row row, Optional expected) {
		DefaultHandler handler = new DefaultHandler(Collections.emptyList());
		Optional<String> value = handler.getValue(column, row);
		Assert.assertEquals(message, expected, value);
	}
}