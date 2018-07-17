package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.*;

public class MapIndexKeyMapperTest {

	List<BindColumn> columns;
	MapIndexKeyMapper mapper;

	@Before
	public void initDate() {
		BindColumn column = new BindColumn(1, "test-1", "1");
		BindColumn column2 = new BindColumn(2, "test-2", "2");
		BindColumn column3 = new BindColumn(3, "test-3", "3");
		columns = Lists.newArrayList(column, column2, column3);
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType contentType = typeFactory.constructType(String.class);
		mapper = new MapIndexKeyMapper("1", contentType, columns, Collections.emptyList());
	}

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);

			row.createCell(3);
			Map<Integer, String> value = Maps.newHashMap();
			check("cells is empty", row, Optional.of(value));

			row.createCell(1).setCellValue("va");
			value.put(1, "va");
			check("cells not empty", row, Optional.of(value));

		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, Row row, Optional expected) {
		Optional<Map<Integer, Object>> value = mapper.getValue(row);
		Assert.assertEquals(message, expected, value);
	}

	@Test
	public void getContents() {
		Map<Integer, String> value = Maps.newHashMap();
		value.put(1, "va");
		value.put(2, "va1");
		value.put(3, "va2");
		List<ExcelContent> contents = mapper.getContents(value);
		Assert.assertEquals("map<Integer,String> to  contents", 3, contents.size());
		contents.sort(Comparator.comparing(ExcelContent::getColumnIndex));
		List<ExcelContent> expected = Lists.newArrayList();
		expected.add(new ExcelContent(1, "va"));
		expected.add(new ExcelContent(2, "va1"));
		expected.add(new ExcelContent(3, "va2"));
		Assert.assertEquals("map<Integer,String> to  contents", expected, contents);
	}
}