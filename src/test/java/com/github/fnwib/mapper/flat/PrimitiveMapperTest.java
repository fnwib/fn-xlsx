package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.model.ExcelContent;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class PrimitiveMapperTest {


	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0);
			row.createCell(1).setCellValue("test1");
			row.createCell(2).setCellValue(new Date());
			check("null", String.class, 0, row, Optional.empty());
			check("test1", String.class, 1, row, Optional.of("test1"));
			check("test1", LocalDate.class, 2, row, Optional.of(LocalDate.now()));
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, Class<?> type, int column, Row row, Optional expected) {
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType javaType = typeFactory.constructType(type);
		BindColumn bindColumn = new BindColumn(column, "test -", "1");
		PrimitiveMapper mapper = new PrimitiveMapper(javaType, bindColumn, Collections.emptyList());
		Optional<?> value = mapper.getValue(row);
		Assert.assertEquals(message, expected, value);
	}


	@Test
	public void getContents() {
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType javaType = typeFactory.constructType(String.class);
		BindColumn column = new BindColumn(1, "test -", "1");
		PrimitiveMapper mapping = new PrimitiveMapper(javaType, column, Collections.emptyList());
		List<ExcelContent> contents = mapping.getContents("va");
		Assert.assertEquals("string value to  contents", 1, contents.size());
		Assert.assertEquals("string value to  contents", new ExcelContent(1, "va"), contents.get(0));
	}
}