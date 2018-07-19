package com.github.fnwib.mapper.flat;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.model.Content;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public class CollectionCellMapperTest {

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
		CollectionCellMapper mapper = new CollectionCellMapper("1", columns);
		List<BindColumn> columns = mapper.getColumns();
		Assert.assertEquals("columns", this.columns, columns);
	}

	@Test
	public void getValue() {
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			Cell cell1 = row.createCell(1);
			Cell cell2 = row.createCell(2);
			cell2.setCellValue("string");
			Cell cell3 = row.createCell(3);
			cell3.setCellValue(new Date());
			List<Cell> cells = Lists.newArrayList(cell1, cell2, cell3);
			check("cells", row, Optional.of(cells));
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}

	private void check(String message, Row row, Optional expected) {
		CollectionCellMapper mapper = new CollectionCellMapper("1", columns);
		Optional<List<Cell>> value = mapper.getValue(row);
		Assert.assertEquals(message, expected, value);
	}

	@Test
	public void getContents() {
		CollectionCellMapper mapper = new CollectionCellMapper("1", columns);
		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			Cell cell1 = row.createCell(1);
			Cell cell2 = row.createCell(2);
			cell2.setCellValue("string");
			Cell cell3 = row.createCell(3);
			cell3.setCellValue(new Date());
			List<Cell> cells = Lists.newArrayList(cell1, cell2, cell3);
			List<Content> contents = mapper.getContents(cells);
			contents.sort(Comparator.comparing(Content::getColumnIndex));

			Assert.assertEquals("string value to  contents", 3, contents.size());

			List<Content> expected = Lists.newArrayList();
			expected.add(new Content(cell1));
			expected.add(new Content(cell2));
			expected.add(new Content(cell3));
			Assert.assertEquals("map<Integer,String> to  contents", expected, contents);
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}
}