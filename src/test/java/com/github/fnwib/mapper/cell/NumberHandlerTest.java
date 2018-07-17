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

import static org.junit.Assert.*;

public class NumberHandlerTest {

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0);
			row.createCell(1).setCellValue("1");
			check("null", 0, row, Optional.empty());
			check("int 1", 1, row, Optional.of("1"));
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, int column, Row row, Optional<String> expected) {
		NumberHandler handler = new NumberHandler();
		Optional<String> value = handler.getValue(column, row);
		Assert.assertEquals(message, expected, value);
	}

}