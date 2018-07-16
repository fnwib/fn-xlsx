package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.model.ExcelContent;
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

public class MapTextKeyMapperTest {

	List<BindColumn> columns;
	MapTextKeyMapper mapper;

	@Before
	public void initDate() {
		BindColumn column = new BindColumn(1, "test-1", "1");
		BindColumn column2 = new BindColumn(2, "test-2", "2");
		BindColumn column3 = new BindColumn(3, "test-3", "3");
		columns = Lists.newArrayList(column, column2, column3);
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType contentType = typeFactory.constructType(String.class);
		mapper = new MapTextKeyMapper("1", contentType, columns, Collections.emptyList());
	}

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);

			row.createCell(3);
			Map<String, String> value = Maps.newHashMap();
			check("cells is empty", row, Optional.of(value));

			row.createCell(1).setCellValue("va");
			value.put("test-1", "va");
			check("cells not empty", row, Optional.of(value));

		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, Row row, Optional expected) {
		Optional<Map<String, Object>> value = mapper.getValue(row);
		Assert.assertEquals(message, expected, value);
	}


	@Test
	public void getContents() {
		String key1 = UUIDUtils.getHalfId();
		String key2 = UUIDUtils.getHalfId();
		String key3 = UUIDUtils.getHalfId();
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType javaType = typeFactory.constructMapType(Map.class, String.class, String.class);
		BindColumn column1 = new BindColumn(1, key1, "1");
		BindColumn column2 = new BindColumn(2, key2, "2");
		BindColumn column3 = new BindColumn(3, key3, "3");
		List<BindColumn> columns = Lists.newArrayList(column1, column2, column3);
		AbstractContainerMapper mapping = new MapTextKeyMapper("1", javaType, columns, Collections.emptyList());
		Map<String, String> value = Maps.newHashMap();
		value.put(key1, "va");
		value.put(key2, "va1");
		value.put(key3, "va2");
		List<ExcelContent> contents = mapping.getContents(value);
		Assert.assertEquals("map<String,String> to  contents", 3, contents.size());
		contents.sort(Comparator.comparing(ExcelContent::getColumnIndex));
		List<ExcelContent> expected = Lists.newArrayList();
		expected.add(new ExcelContent(1, "va"));
		expected.add(new ExcelContent(2, "va1"));
		expected.add(new ExcelContent(3, "va2"));
		Assert.assertEquals("map<String,String> to  contents", expected, contents);
	}
}