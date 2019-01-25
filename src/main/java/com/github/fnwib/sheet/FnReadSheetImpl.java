package com.github.fnwib.sheet;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.mapper.RowMapper;
import com.github.fnwib.model.FnRow;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;

import java.util.*;

/**
 * @param <T>
 * @author fengweibin
 */
@Slf4j
public class FnReadSheetImpl<T> implements FnReadSheet<T> {
	private final Map<Integer, Row> beforeHeader;
	private Row title;
	private int titleNum;
	private final RowMapper<T> mapper;
	private final Iterator<Row> iterator;
	private final int lastRowNum;

	public FnReadSheetImpl(RowMapper<T> mapper, Sheet sheet) {
		this.mapper = mapper;
		this.lastRowNum = sheet.getLastRowNum();
		this.iterator = sheet.iterator();
		this.titleNum = -1;
		this.beforeHeader = new HashMap<>();
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
	public Iterator<FnRow<T>> iterator() {
		if (!findTitle()) {
			throw new ExcelException("模版错误");
		}
		return new Itr<>(mapper, iterator);
	}

	private static class Itr<T> implements Iterator<FnRow<T>> {
		private final RowMapper<T> mapper;
		private final Iterator<Row> iterator;

		public Itr(RowMapper<T> mapper, Iterator<Row> iterator) {
			this.mapper = mapper;
			this.iterator = iterator;
		}

		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public FnRow<T> next() {
			Row row = iterator.next();
			if (mapper.isEmpty(row)) {
				return new FnRow<>(row, "空行");
			}
			try {
				Optional<T> op = mapper.convert(row);
				if (op.isPresent()) {
					return new FnRow<>(row, op.get());
				} else {
					return new FnRow<>(row, "空行");
				}
			} catch (ExcelException e) {
				return new FnRow<>(row, e.getMessage());
			} catch (IllegalArgumentException e) {
				log.error("param error {}", e);
				return new FnRow<>(row, e.getMessage());
			} catch (Exception e) {
				log.error("unknown error {}", e);
				return new FnRow<>(row, e.getMessage());
			}
		}
	}
}
