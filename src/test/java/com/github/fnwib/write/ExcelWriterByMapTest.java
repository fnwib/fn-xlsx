package com.github.fnwib.write;

import com.github.fnwib.model.Content;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.PreHeader;
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

public class ExcelWriterByMapTest extends CommonPath {

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
		List<Header> headers = getHeaders(10);
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.fileName("test")
				.maxRowNumCanWrite(5)
				.sheetName("test-sheet")
				.addPreHeader(PreHeader.builder().rowNum(0).columnIndex(0).value("标题").build())
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
		List<Header> headers = getHeaders(10);
		SheetConfig config = SheetConfig.builder()
				.dir(basePath)
				.fileName("test")
				.maxRowNumCanWrite(5)
				.sheetName("test-sheet")
				.addPreHeader(PreHeader.builder().rowNum(0).columnIndex(0).value("标题").build())
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

		public MappingImpl(List<Header> headers) {
			ss = headers.stream().collect(Collectors.toMap(Header::getId, Header::getColumnIndex));
		}

		@Override
		public List<Content> convert(Map<String, Object> map) {
			List<Content> contents = Lists.newArrayListWithCapacity(map.size());
			map.forEach((k, v) -> {
				Integer integer = ss.get(k);
				if (integer == null) {
					throw new RuntimeException();
				}
				if (v.getClass() == String.class) {
					Content content = new Content(integer, v.toString());
					contents.add(content);
				}
			});
			return contents;
		}
	}
}