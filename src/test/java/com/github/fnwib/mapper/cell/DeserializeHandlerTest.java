package com.github.fnwib.mapper.cell;

import com.github.fnwib.databing.deser.LocalDateCellDeserializer;
import com.github.fnwib.exception.ExcelException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Optional;

public class DeserializeHandlerTest {

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0);
			row.createCell(1).setCellValue(new Date());
			row.createCell(2).setCellValue(LocalDate.now().toString());
			check("null", 0, row, Optional.empty());
			check("date", 1, row, Optional.of(LocalDate.now()));
			check("LocalDate", 2, row, Optional.of(LocalDate.now()));
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	@Test(expected = ExcelException.class)
	public void exception() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(1).setCellValue("2017-12-13");
			row.createCell(3).setCellValue("2017-13-13");
			DeserializeHandler handler = new DeserializeHandler(new LocalDateCellDeserializer());
			handler.getValue(1, row);
			handler.getValue(3, row);
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}


	private void check(String message, int column, Row row, Optional expected) {
		DeserializeHandler handler = new DeserializeHandler(new LocalDateCellDeserializer());
		Optional<?> value = handler.getValue(column, row);
		Assert.assertEquals(message, expected, value);
	}
}