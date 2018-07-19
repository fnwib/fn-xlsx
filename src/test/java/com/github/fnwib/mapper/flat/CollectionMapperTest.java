package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
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
import java.util.*;

public class CollectionMapperTest {

	List<BindColumn> columns;

	@Before
	public void initDate() {
		BindColumn column = new BindColumn(1, "test-1", "1");
		BindColumn column2 = new BindColumn(2, "test-2", "2");
		BindColumn column3 = new BindColumn(3, "test-3", "3");
		columns = Lists.newArrayList(column, column2, column3);
	}

	@Test
	public void getColumns() {
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType contentType = typeFactory.constructType(String.class);
		CollectionMapper mapper = new CollectionMapper("1", contentType, columns, Collections.emptyList());
		List<BindColumn> columns = mapper.getColumns();
		Assert.assertEquals("columns", this.columns, columns);
	}

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(1);
			row.createCell(2).setCellValue("string");
			row.createCell(3).setCellValue("value");
			List<Object> cells = Lists.newArrayList(null, "string", "value");
			check("cells", row, Optional.of(cells));
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, Row row, Optional expected) {
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType contentType = typeFactory.constructType(String.class);
		CollectionMapper mapper = new CollectionMapper("1", contentType, columns, Collections.emptyList());
		Optional<Collection<Object>> value = mapper.getValue(row);
		Assert.assertEquals(message, expected, value);
	}

	@Test
	public void getContents() {
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType contentType = typeFactory.constructType(String.class);
		CollectionMapper mapper = new CollectionMapper("1", contentType, columns, Collections.emptyList());
		List<Integer> value = Lists.newArrayList(null, 1, 2);
		List<Content> contents = mapper.getContents(value);
		Assert.assertEquals("string value to  contents", 3, contents.size());
		contents.sort(Comparator.comparing(Content::getColumnIndex));
		List<Content> expected = Lists.newArrayList();
		expected.add(new Content(1, null));
		expected.add(new Content(2, "1"));
		expected.add(new Content(3, "2"));
		Assert.assertEquals("List<String> to  contents", expected, contents);
	}
}