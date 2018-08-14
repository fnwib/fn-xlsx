package com.github.fnwib.write;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapper.RowMapper;
import com.github.fnwib.model.Content;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.RowContent;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.util.FnUtils;
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
	private RowMapper<T> mapper;
	private FnSheet fnSheet;
	private boolean closed;

	public ExcelWriterImpl(SheetConfig sheetConfig, RowMapper<T> mapper) {
		this(sheetConfig, mapper, null);
	}

	public ExcelWriterImpl(SheetConfig sheetConfig, RowMapper<T> mapper, File template) {
		this.sheetConfig = sheetConfig;
		this.mapper = mapper;
		FnUtils.merge(sheetConfig, template, mapper);
		List<Header> headers = sheetConfig.getHeaders();
		boolean match = this.mapper.match(headers);
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
		List<Content> contents = mapper.convert(element);
		fnSheet.addRow(new RowContent(contents));
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
			List<RowContent> rows = Lists.newArrayListWithCapacity(elements.size());
			for (T element : elements) {
				List<Content> contents = mapper.convert(element);
				rows.add(new RowContent(contents));
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
		if (fnSheet != null) {
			fnSheet.flush();
		}
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

