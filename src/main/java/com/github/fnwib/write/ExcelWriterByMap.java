package com.github.fnwib.write;

import com.github.fnwib.model.Content;
import com.github.fnwib.model.RowContent;
import com.github.fnwib.model.SheetConfig;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelWriterByMap implements ExcelWriter<Map<String, Object>> {
	private RowContentWriter writer;

	private Mapping mapping;

	public ExcelWriterByMap(SheetConfig sheetConfig, Mapping mapping) {
		this.writer = new RowContentWriter(sheetConfig);
		this.mapping = mapping;
	}

	@Override
	public void write(Map<String, Object> element) {
		if (element == null || element.isEmpty()) {
			return;
		}
		List<Content> contents = mapping.convert(element);
		writer.write(new RowContent(contents));
	}

	@Override
	public void write(List<Map<String, Object>> elements) {
		for (Map<String, Object> element : elements) {
			write(element);
		}
	}

	@Override
	public void writeMergedRegion(List<Map<String, Object>> elements, List<Integer> mergeIndexes) {
		if (elements.isEmpty()) {
			return;
		}
		if (elements.size() == 1) {
			write(elements);
		} else {
			List<RowContent> rows = Lists.newArrayListWithCapacity(elements.size());
			for (Map<String, Object> element : elements) {
				List<Content> convert = mapping.convert(element);
				rows.add(new RowContent(convert));
			}
			writer.writeMergedRegion(rows,mergeIndexes);
		}
	}

	@Override
	public void flush() {
		writer.flush();
	}

	@Override
	public List<File> getFiles() {
		return writer.getFiles();
	}
}
