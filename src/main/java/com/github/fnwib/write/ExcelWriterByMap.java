package com.github.fnwib.write;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.write.fn.FnSheet;
import com.github.fnwib.write.fn.SingleSheetImpl;
import com.github.fnwib.write.model.ExcelContent;
import com.github.fnwib.write.model.ExcelHeader;
import com.github.fnwib.write.model.RowExcelContent;
import com.github.fnwib.write.model.SheetConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
public class ExcelWriterByMap implements ExcelWriter<Map<String, String>> {
	private final SheetConfig sheetConfig;
	private final Map<String, Integer> mapping;
	private FnSheet fnSheet;
	private boolean closed;

	public ExcelWriterByMap(SheetConfig sheetConfig) {
		this.sheetConfig = sheetConfig;
		this.mapping = mapping(sheetConfig.getHeaders());
		this.closed = false;

	}

	public Map<String, Integer> mapping(List<ExcelHeader> headers) {
		Map<String, Integer> mapping = Maps.newHashMap();
		for (ExcelHeader header : headers) {
			mapping.put(header.getId(), header.getColumnIndex());
		}
		return mapping;
	}

	private void check(int size) {
		if (closed) {
			throw new ExcelException("已经关闭");
		}

		if (fnSheet == null) {
			fnSheet = new SingleSheetImpl(sheetConfig);
		}
		if (fnSheet.canWriteSize() < size) {
			log.debug("需要写入'{}'行, 当前sheet可写入行'{}'不足,将创建一个新sheet", size, fnSheet.canWriteSize());
			fnSheet.flush();
			fnSheet = new SingleSheetImpl(sheetConfig);
			if (fnSheet.canWriteSize() < size) {
				throw new SettingException("Sheet起始可写入rowNum'%s'，最大可写入rowNum '%s'。请检查配置", fnSheet.getStartRow(), sheetConfig.getMaxRowNumCanWrite());
			}
		}

	}

	@Override
	public void write(Map<String, String> element) {
		if (element == null || element.isEmpty()) {
			return;
		}
		check(1);
		fnSheet.addRow(convert(element));
	}

	private RowExcelContent convert(Map<String, String> element) {
		List<ExcelContent> cells = Lists.newArrayListWithCapacity(element.size());
		element.forEach((k, v) -> {
			Integer index = mapping.get(k);
			if (index == null) {
				log.warn("k {}", k);
				log.warn("element {}", element);
				log.warn("mapping {}", mapping);
				return;
			}
			cells.add(new ExcelContent(index, v));
		});
		return new RowExcelContent(cells);
	}

	@Override
	public void write(List<Map<String, String>> elements) {
		for (Map<String, String> element : elements) {
			write(element);
		}
	}

	@Override
	public void writeMergedRegion(List<Map<String, String>> elements, List<Integer> mergeIndexes) {
		if (elements.isEmpty()) {
			return;
		}
		if (elements.size() == 1) {
			write(elements);
		} else {
			check(elements.size());
			List<RowExcelContent> rows = Lists.newArrayListWithCapacity(elements.size());
			for (Map<String, String> element : elements) {
				rows.add(convert(element));
			}
			fnSheet.addMergeRow(rows, mergeIndexes);
		}
	}

	@Override
	public void flush() {
		if (closed) {
			return;
		}
		closed = true;
		fnSheet.flush();
	}

	@Override
	public List<File> getFiles() {
		if (!closed) {
			flush();
		}
		try {
			return Files.list(sheetConfig.getDir()).map(Path::toFile).collect(Collectors.toList());
		} catch (IOException e) {
			log.error("error {}", e);
			return Collections.emptyList();
		}
	}
}
