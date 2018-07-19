package com.github.fnwib.mapper.nested;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.Mappers;
import com.github.fnwib.model.Content;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.HeaderCreator;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

public class NestedMapperTest {

	NestedMapper<TestNestedModel> nestedMapper;

	@Before
	public void initDate() {
		List<Header> headers = HeaderCreator.create(new AtomicInteger(), "list 1", "list 2", "list 3", "map 1", "map 2", "map 3");
		nestedMapper = Mappers.createNestedMapper(TestNestedModel.class, new LocalConfig(), headers);
	}


	@Test
	public void getColumns() {
	}

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0);
			row.createCell(1).setCellValue("val2");
			row.createCell(2).setCellValue("val3");
			row.createCell(3).setCellValue("m1");
			row.createCell(4);
			row.createCell(5).setCellValue("m3");

			TestNestedModel object = new TestNestedModel();
			object.setList(Lists.newArrayList(null, "val2", "val3"));
			Map<Integer, String> map = Maps.newHashMap();
			map.put(3, "m1");
			map.put(5, "m3");
			object.setMap(map);

			check("nested", row, Optional.of(object));
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, Row row, Optional expected) {
		Optional<TestNestedModel> value = nestedMapper.getValue(row);
		Assert.assertEquals(message, expected, value);
	}

	@Test
	public void getContents() {
		TestNestedModel object = new TestNestedModel();
		object.setList(Lists.newArrayList(null, "val2", "val3"));
		Map<Integer, String> map = Maps.newHashMap();
		map.put(3, "m1");
		map.put(4, null);
		map.put(5, "m3");
		object.setMap(map);

		List<Content> contents = nestedMapper.getContents(object);
		Assert.assertEquals("TestNestedModel to  contents", 6, contents.size());
		contents.sort(Comparator.comparing(Content::getColumnIndex));
		List<Content> expected = Lists.newArrayList();
		expected.add(new Content(0, null));
		expected.add(new Content(1, "val2"));
		expected.add(new Content(2, "val3"));
		expected.add(new Content(3, "m1"));
		expected.add(new Content(4, null));
		expected.add(new Content(5, "m3"));


		Assert.assertEquals("TestNestedModel to  contents", expected, contents);
	}

	@Getter
	@Setter
	@EqualsAndHashCode
	@ToString
	private static class TestNestedModel {
		@AutoMapping(prefix = "list", value = "\\d+")
		private List<String> list;
		@AutoMapping(prefix = "map", value = "\\d+")
		private Map<Integer, String> map;
	}

}