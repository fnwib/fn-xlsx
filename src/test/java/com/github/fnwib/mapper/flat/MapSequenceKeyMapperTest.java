package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.jackson.Sequence;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.model.Content;
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

public class MapSequenceKeyMapperTest {

	List<BindColumn> columns;
	MapSequenceKeyMapper mapper;

	@Before
	public void initDate() {
		BindColumn column = new BindColumn(1, "test-1", "1");
		BindColumn column2 = new BindColumn(2, "test-2", "2");
		BindColumn column3 = new BindColumn(3, "test-3", "3");
		columns = Lists.newArrayList(column, column2, column3);
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType contentType = typeFactory.constructType(String.class);
		mapper = new MapSequenceKeyMapper("1", contentType, columns, Collections.emptyList());
	}

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);

			row.createCell(3);
			Map<Sequence, String> value = Maps.newHashMap();
			check("cells is empty", row, Optional.of(value));

			row.createCell(1).setCellValue("va");
			value.put(new Sequence(1), "va");
			check("cells not empty", row, Optional.of(value));

		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, Row row, Optional expected) {
		Optional<Map<Sequence, Object>> value = mapper.getValue(row);
		Assert.assertEquals(message, expected, value);
	}

	@Test
	public void getContents() {
		Map<Sequence, String> value = Maps.newHashMap();
		value.put(new Sequence(1), "va");
		value.put(new Sequence(2), null);
		value.put(new Sequence(3), "va2");
		List<Content> contents = mapper.getContents(value);
		Assert.assertEquals("map<Sequence,String> to  contents", 3, contents.size());
		contents.sort(Comparator.comparing(Content::getColumnIndex));
		List<Content> expected = Lists.newArrayList();
		expected.add(new Content(1, "va"));
		expected.add(new Content(2, null));
		expected.add(new Content(3, "va2"));
		Assert.assertEquals("map<Sequence,String> to  contents", expected, contents);
	}

}