package com.github.fnwib.write;

import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.config.ResultFileSetting;
import com.github.fnwib.write.model.ExcelHeader;
import com.github.fnwib.write.model.ExcelPreHeader;
import com.github.fnwib.write.model.SheetConfig;
import com.google.common.collect.Maps;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WriterByMapTest {

	private String basePath;

	@Before
	public void initDate() throws IOException {
		ClassLoader classLoader = getClass().getClassLoader();
		String dir = classLoader.getResource("test-file").getFile();
		Path path = Paths.get(dir, UUIDUtils.getHalfId());
		Files.createDirectory(path);
		basePath = path.toString();
	}

	private List<ExcelHeader> getHeaders(int size) {
		List<ExcelHeader> headers = new ArrayList<>(size);
		for (int i = 0; i < size; i++) {
			ExcelHeader build = ExcelHeader.builder()
					.id("id:" + i)
					.cellIndex(i)
					.value("head " + i)
					.build();
			headers.add(build);
		}
		return headers;
	}

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
				.maxRowsCanWrite(5)
				.sheetName("test-sheet")
				.addPreHeader(ExcelPreHeader.builder().rowNum(0).cellIndex(0).value("标题").build())
				.addHeaders(getHeaders(10))
				.build();
		ExcelWriter<Map<String, String>> writer = new WriterByMap(config);
		writer.write(getRows(15, 10));
		List<File> files = writer.getFiles();
		for (File file : files) {
			System.out.println(file.getAbsoluteFile());
		}
	}

	@Test
	public void writeList() throws IOException {

	}

	@Test
	public void writeMergedRegion() {
	}
}