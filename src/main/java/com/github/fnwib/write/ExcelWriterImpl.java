package com.github.fnwib.write;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapper.RowMapper;
import com.github.fnwib.model.Content;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.RowContent;
import com.github.fnwib.model.SheetConfig;
import com.github.fnwib.util.FnUtils;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.List;

/**
 * Excel生成工具
 * <p>
 *
 * @param <T>
 */
@Slf4j
public class ExcelWriterImpl<T> extends AbstractWriter<T> {

	private RowMapper<T> mapper;

	public ExcelWriterImpl(SheetConfig sheetConfig, RowMapper<T> mapper) {
		this(sheetConfig, mapper, null);
	}

	public ExcelWriterImpl(SheetConfig sheetConfig, RowMapper<T> mapper, File template) {
		super(sheetConfig);
		this.mapper = mapper;
		FnUtils.merge(sheetConfig, template, mapper);
		List<Header> headers = sheetConfig.getHeaders();
		boolean match = this.mapper.match(headers);
		if (!match) {
			throw new SettingException("未知错误");
		}
		this.closed = false;
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
		if (elements.size() == 1) {
			this.write(elements.get(0));
		} else if (elements.size() > 1) {
			check(elements.size());
			List<RowContent> rows = Lists.newArrayListWithCapacity(elements.size());
			for (T element : elements) {
				List<Content> contents = mapper.convert(element);
				rows.add(new RowContent(contents));
			}
			fnSheet.addMergeRow(rows, mergedRangeIndexes);
		}
	}
}

