package com.github.fnwib.mapper.cell;

import com.github.fnwib.exception.ExcelException;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Test;

import java.io.IOException;

public class ErrorCellTypeTest {

	@Test(expected = ExcelException.class)
	public void noneOrBlankCellType() throws IOException {
		try (Workbook sheets = new XSSFWorkbook()) {
			Sheet fromSheet = sheets.createSheet();
			Row fromRow = fromSheet.createRow(0);
			Cell none = fromRow.createCell(0);
			throw ErrorCellType.STRING_TO_DATE.getException(none);
		}
	}


	@Test(expected = ExcelException.class)
	public void errorCellType() throws IOException {
		try (Workbook sheets = new XSSFWorkbook()) {
			Sheet fromSheet = sheets.createSheet();
			Row fromRow = fromSheet.createRow(0);
			Cell error = fromRow.createCell(2);
			error.setCellErrorValue((FormulaError.NAME.getCode()));
			throw ErrorCellType.STRING_TO_DATE.getException(error);
		}
	}


	@Test(expected = ExcelException.class)
	public void boolCellType() throws IOException {
		try (Workbook sheets = new XSSFWorkbook()) {
			Sheet fromSheet = sheets.createSheet();
			Row fromRow = fromSheet.createRow(0);
			Cell bool = fromRow.createCell(3);
			bool.setCellValue(true);
			throw ErrorCellType.STRING_TO_DATE.getException(bool);
		}
	}


	@Test(expected = ExcelException.class)
	public void formulaCellType() throws IOException {
		try (Workbook sheets = new XSSFWorkbook()) {
			Sheet fromSheet = sheets.createSheet();
			Row fromRow = fromSheet.createRow(0);
			Cell formula = fromRow.createCell(4);
			formula.setCellFormula("A1+B1");
			throw ErrorCellType.STRING_TO_DATE.getException(formula);
		}
	}


	@Test(expected = ExcelException.class)
	public void numericCellType() throws IOException {
		try (Workbook sheets = new XSSFWorkbook()) {
			Sheet fromSheet = sheets.createSheet();
			Row fromRow = fromSheet.createRow(0);
			Cell numeric = fromRow.createCell(5);
			numeric.setCellValue(11);
			throw ErrorCellType.STRING_TO_DATE.getException(numeric);
		}
	}


	@Test(expected = ExcelException.class)
	public void stringCellType() throws IOException {
		try (Workbook sheets = new XSSFWorkbook()) {
			Sheet fromSheet = sheets.createSheet();
			Row fromRow = fromSheet.createRow(0);
			Cell string = fromRow.createCell(6);
			string.setCellValue("val");
			throw ErrorCellType.STRING_TO_DATE.getException(string);
		}
	}

}