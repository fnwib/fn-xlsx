package com.github.fnwib.write;

import com.github.fnwib.model.ExcelPreHeader;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.util.UUIDUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelWriterByMapTest extends CommonPathTest{

	private List<Map<String, String>> getRows(int rowSize, int columnSize) {
		List<Map<String, String>> rows = new ArrayList<>(rowSize);
		for (int i = 0; i < rowSize; i++) {
			Map<String, String> row = Maps.newHashMap();
			for (int j = 0; j < columnSize; j++) {
				row.put("id:" + j, UUIDUtils.getHalfId());
			}
			rows.add(row);
		}
		return rows;
	}

	@Test
	public void write() {
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.fileName("test")
				.maxRowNumCanWrite(5)
				.sheetName("test-sheet")
				.addPreHeader(ExcelPreHeader.builder().rowNum(0).columnIndex(0).value("标题").build())
				.addHeaders(getHeaders(10))
				.build();
		ExcelWriter<Map<String, String>> writer = new ExcelWriterByMap(config);
		writer.write(getRows(15, 10));
		List<File> files = writer.getFiles();
		for (File file : files) {
			System.out.println(file.getAbsoluteFile());
		}
	}

	@Test
	public void writeMergedRegion() {
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.fileName("test")
				.maxRowNumCanWrite(5)
				.sheetName("test-sheet")
				.addPreHeader(ExcelPreHeader.builder().rowNum(0).columnIndex(0).value("标题").build())
				.addHeaders(getHeaders(10))
				.build();
		ExcelWriter<Map<String, String>> writer = new ExcelWriterByMap(config);
		writer.writeMergedRegion(getRows(3, 10), Lists.newArrayList(1, 2, 3, 4));
		writer.writeMergedRegion(getRows(2, 10), Lists.newArrayList(1, 2, 3, 4));
		writer.writeMergedRegion(getRows(3, 10), Lists.newArrayList(1, 2, 3, 4));
		writer.writeMergedRegion(getRows(3, 10), Lists.newArrayList(1, 2, 3, 4));
		List<File> files = writer.getFiles();
		for (File file : files) {
			System.out.println(file.getAbsoluteFile());
		}
	}
}