package com.github.fnwib.write;

import com.github.fnwib.model.ExcelContent;
import com.github.fnwib.model.ExcelHeader;
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
import java.util.stream.Collectors;

public class ExcelWriterByMapTest extends CommonPathTest {

	private List<Map<String, Object>> getRows(int rowSize, int columnSize) {
		List<Map<String, Object>> rows = new ArrayList<>(rowSize);
		for (int i = 0; i < rowSize; i++) {
			Map<String, Object> row = Maps.newHashMap();
			for (int j = 0; j < columnSize; j++) {
				row.put("id:" + j, UUIDUtils.getHalfId());
			}
			rows.add(row);
		}
		return rows;
	}

	@Test
	public void write() {
		List<ExcelHeader> headers = getHeaders(10);
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.fileName("test")
				.maxRowNumCanWrite(5)
				.sheetName("test-sheet")
				.addPreHeader(ExcelPreHeader.builder().rowNum(0).columnIndex(0).value("标题").build())
				.addHeaders(headers)
				.build();
		Mapping mapping = new MappingImpl(headers);
		ExcelWriter<Map<String, Object>> writer = new ExcelWriterByMap(config, mapping);
		writer.write(getRows(15, 10));
		List<File> files = writer.getFiles();
		for (File file : files) {
			System.out.println(file.getAbsoluteFile());
		}
	}

	@Test
	public void writeMergedRegion() {
		List<ExcelHeader> headers = getHeaders(10);
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.fileName("test")
				.maxRowNumCanWrite(5)
				.sheetName("test-sheet")
				.addPreHeader(ExcelPreHeader.builder().rowNum(0).columnIndex(0).value("标题").build())
				.addHeaders(headers)
				.build();
		Mapping mapping = new MappingImpl(headers);
		ExcelWriter<Map<String, Object>> writer = new ExcelWriterByMap(config, mapping);
		writer.writeMergedRegion(getRows(3, 10), Lists.newArrayList(1, 2, 3, 4));
		writer.writeMergedRegion(getRows(2, 10), Lists.newArrayList(1, 2, 3, 4));
		writer.writeMergedRegion(getRows(3, 10), Lists.newArrayList(1, 2, 3, 4));
		writer.writeMergedRegion(getRows(3, 10), Lists.newArrayList(1, 2, 3, 4));
		List<File> files = writer.getFiles();
		for (File file : files) {
			System.out.println(file.getAbsoluteFile());
		}
	}

	static class MappingImpl implements Mapping {

		Map<String, Integer> ss;

		public MappingImpl(List<ExcelHeader> headers) {
			ss = headers.stream().collect(Collectors.toMap(ExcelHeader::getId, ExcelHeader::getColumnIndex));
		}

		@Override
		public List<ExcelContent> convert(Map<String, Object> map) {
			List<ExcelContent> contents = Lists.newArrayListWithCapacity(map.size());
			map.forEach((k, v) -> {
				Integer integer = ss.get(k);
				if (integer == null) {
					throw new RuntimeException();
				}
				if (v.getClass() == String.class) {
					ExcelContent content = new ExcelContent(integer, v.toString());
					contents.add(content);
				}
			});
			return contents;
		}
	}
}