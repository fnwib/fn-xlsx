package com.github.fnwib.write;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapper.RowReader;
import com.github.fnwib.model.ExcelHeader;
import com.github.fnwib.model.RowExcelContent;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.util.FnUtils;
import com.github.fnwib.write.config.WorkbookConfig;
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
import java.util.stream.Collectors;

/**
 * Excel生成工具
 * <p>
 *
 * @param <T>
 */
@Slf4j
public class ExcelWriterImpl<T> implements ExcelWriter<T> {

	private final SheetConfig sheetConfig;
	private RowReader<T> reader;
	private FnSheet fnSheet;
	private boolean closed;

	@Deprecated
	public ExcelWriterImpl(WorkbookConfig workbookConfig) {
		ComUtils<T> comUtils = new ComUtils<>(workbookConfig);
		sheetConfig = comUtils.toSheetConfig();
		reader = comUtils.getRowReader();
		List<ExcelHeader> headers = sheetConfig.getHeaders();
		boolean match = this.reader.match(headers);
		if (!match) {
			throw new SettingException("未知错误");
		}
		this.closed = false;
	}

	public ExcelWriterImpl(SheetConfig sheetConfig, RowReader<T> reader) {
		this(sheetConfig, reader, null);
	}

	public ExcelWriterImpl(SheetConfig sheetConfig, RowReader<T> reader, File template) {
		this.sheetConfig = sheetConfig;
		this.reader = reader;
		FnUtils.merge(sheetConfig, template, reader);
		List<ExcelHeader> headers = sheetConfig.getHeaders();
		boolean match = this.reader.match(headers);
		if (!match) {
			throw new SettingException("未知错误");
		}
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
	public void write(T element) {
		check(1);
		fnSheet.addRow(reader.convert(element));
	}

	@Override
	public void write(List<T> elements) {
		for (T element : elements) {
			this.write(element);
		}
	}

	@Override
	public void writeMergedRegion(List<T> elements, List<Integer> mergedRangeIndexes) {
		if (elements.isEmpty()) {
			return;
		} else if (elements.size() == 1) {
			this.write(elements.get(0));
		} else {
			check(elements.size());
			List<RowExcelContent> rows = Lists.newArrayListWithCapacity(elements.size());
			for (T element : elements) {
				rows.add(reader.convert(element));
			}
			fnSheet.addMergeRow(rows, mergedRangeIndexes);
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

