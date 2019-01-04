package com.github.fnwib.read;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.RowMapper;
import com.github.fnwib.model.FnRow;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.*;

/**
 * @param <T>
 * @author fengweibin
 */
@Slf4j
public class FnRowReaderImpl<T> implements FnRowReader<T> {
	private final Map<Integer, Row> beforeHeader;
	private Row title;
	private int titleNum;
	private final RowMapper<T> mapper;
	private final Workbook workbook;
	private final Sheet sheet;
	private final Iterator<Row> iterator;
	private final int lastRowNum;
	private boolean closed = false;

	public FnRowReaderImpl(RowMapper<T> mapper, Workbook workbook, int sheetNum) {
		this.mapper = mapper;
		this.workbook = workbook;
		this.sheet = workbook.getSheetAt(Math.max(sheetNum, 0));
		this.lastRowNum = sheet.getLastRowNum();
		this.iterator = sheet.iterator();
		this.titleNum = -1;
		this.beforeHeader = new HashMap<>();
	}

	@Override
	public Sheet getSheet() {
		return sheet;
	}

	@Override
	public List<Row> getBeforeHeader() {
		if (!findTitle()) {
			throw new ExcelException("模版错误");
		}
		List<Row> results = Lists.newArrayListWithCapacity(beforeHeader.size());
		beforeHeader.forEach((k, v) -> results.add(v));
		return results;
	}

	private boolean findTitle() {
		if (titleNum != -1) {
			return true;
		}
		if (lastRowNum == 0 || lastRowNum == titleNum) {
			throw new ExcelException("当前sheet为空");
		}
		while (iterator.hasNext()) {
			Row row = iterator.next();
			if (row.getRowNum() > 10) {
				throw new ExcelException("前十行没有匹配到title");
			}
			boolean match = mapper.match(row);
			if (match) {
				titleNum = row.getRowNum();
				title = row;
				return true;
			} else {
				beforeHeader.put(row.getRowNum(), row);
			}
		}
		return false;
	}

	@Override
	public Row getHeader() {
		if (!findTitle()) {
			throw new ExcelException("模版错误");
		}
		return title;
	}

	@Override
	public boolean hasNext() {
		if (!findTitle()) {
			throw new ExcelException("模版错误");
		}
		boolean hasNext = iterator.hasNext();
		if (!hasNext) {
			close();
		}
		return hasNext;
	}

	@Override
	public FnRow<T> next() {
		if (!findTitle()) {
			throw new ExcelException("模版错误");
		}
		Row next = iterator.next();
		if (mapper.isEmpty(next)) {
			return new FnRow<>(next, "空行");
		}
		try {
			Optional<T> op = mapper.convert(next);
			if (op.isPresent()) {
				return new FnRow<>(next, op.get());
			} else {
				return new FnRow<>(next, "空行");
			}
		} catch (ExcelException e) {
			return new FnRow<>(next, e.getMessage());
		}
	}

	@Override
	public void close() {
		try {
			if (!closed) {
				closed = true;
				workbook.close();
			}
		} catch (IOException e) {
			log.error("workbook can not close {}", e);
		}
	}
}
