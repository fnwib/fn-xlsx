package com.github.fnwib.write;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.model.ExcelContent;
import com.github.fnwib.model.RowExcelContent;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.write.fn.FnSheet;
import com.github.fnwib.write.fn.FnSheetImpl;
import com.google.common.collect.Lists;
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
public class ExcelWriterByMap implements ExcelWriter<Map<String, Object>> {
	private final SheetConfig sheetConfig;
	private FnSheet fnSheet;
	private boolean closed;

	private Mapping mapping;

	public ExcelWriterByMap(SheetConfig sheetConfig, Mapping mapping) {
		this.sheetConfig = sheetConfig;
		this.mapping = mapping;
		this.closed = false;

	}

	private void check(int size) {
		if (closed) {
			throw new ExcelException("已经关闭");
		}

		if (fnSheet == null) {
			fnSheet = new FnSheetImpl(sheetConfig);
		}
		if (fnSheet.canWriteSize() < size) {
			log.debug("需要写入'{}'行, 当前sheet可写入行'{}'不足,将创建一个新sheet", size, fnSheet.canWriteSize());
			fnSheet.flush();
			fnSheet = new FnSheetImpl(sheetConfig);
			if (fnSheet.canWriteSize() < size) {
				throw new SettingException("Sheet起始可写入rowNum'%s'，最大可写入rowNum '%s'。请检查配置", fnSheet.getStartRow(), sheetConfig.getMaxRowNumCanWrite());
			}
		}

	}

	@Override
	public void write(Map<String, Object> element) {
		if (element == null || element.isEmpty()) {
			return;
		}
		check(1);
		List<ExcelContent> convert = mapping.convert(element);
		fnSheet.addRow(convert);
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
			check(elements.size());
			List<RowExcelContent> rows = Lists.newArrayListWithCapacity(elements.size());
			for (Map<String, Object> element : elements) {
				List<ExcelContent> convert = mapping.convert(element);
				rows.add(new RowExcelContent(convert));
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
