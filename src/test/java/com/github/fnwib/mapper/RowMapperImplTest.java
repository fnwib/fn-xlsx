package com.github.fnwib.mapper;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.HeaderCreator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RowMapperImplTest {


	@Test
	public void match() {
		RowMapperImpl<RowMapperBo> mapper = new RowMapperImpl<>(RowMapperBo.class, new LocalConfig(), 1);
		List<Header> headers = HeaderCreator.create(new AtomicInteger(), "value", "value1", "value");
		boolean match = mapper.match(headers);
		Assert.assertTrue("", match);
	}

	static class RowMapperBo {
		@AutoMapping("value")
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

	@Test
	public void getSkipCells() {
		RowMapperImpl<RowMapperBo> mapper = new RowMapperImpl<>(RowMapperBo.class, new LocalConfig(), 1);

		try (Workbook sheets = new SXSSFWorkbook()) {
			Sheet sheet = sheets.createSheet();
			Row row = sheet.createRow(0);
			row.createCell(0);
			row.createCell(1).setCellValue("val2");
			row.createCell(2).setCellValue("val2");
			List<Cell> cells = mapper.getSkipCells(row);
			Assert.assertEquals("skip 2", 2, cells.size());
		} catch (IOException e) {
			throw new ExcelException(e);
		}
	}
}