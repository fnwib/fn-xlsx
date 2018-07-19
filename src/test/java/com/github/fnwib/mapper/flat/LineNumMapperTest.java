package com.github.fnwib.mapper.flat;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.model.Content;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LineNumMapperTest {
	List<BindColumn> columns;

	@Before
	public void initDate() {
		BindColumn column = new BindColumn(1, "test-1", "1");
		columns = Lists.newArrayList(column);
	}

	@Test
	public void getColumns() {
		LineNumMapper mapper = new LineNumMapper(columns);
		List<BindColumn> columns = mapper.getColumns();
		Assert.assertEquals("columns", this.columns, columns);
	}

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			Row row1 = sheet.createRow(1);
			Row row2 = sheet.createRow(2);
			check("lineNum", row, Optional.of(1));
			check("lineNum", row1, Optional.of(2));
			check("lineNum", row2, Optional.of(3));
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, Row row, Optional expected) {
		LineNumMapper mapper = new LineNumMapper(Collections.emptyList());
		Optional<Integer> value = mapper.getValue(row);
		Assert.assertEquals(message, expected, value);
	}

	@Test
	public void getContents() {
		LineNumMapper mapper = new LineNumMapper(columns);
		List<Content> contents = mapper.getContents(1);
		Assert.assertEquals("string value to  contents", 1, contents.size());
		Assert.assertEquals("string value to  contents", new Content(1, "1"), contents.get(0));
	}
}