package com.github.fnwib.read;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

@Slf4j
public class ExcelReaderImpl<T> implements ExcelReader<T> {
	private int TITLE = -1;

	//记录TITLE前的数据
	private final List<Row> preHeaders;

	private final LineReader<T> parser;

	private final Workbook workbook;
	private final Iterator<Row> iterator;
	private final int max;

	public ExcelReaderImpl(LineReader<T> parser, Workbook workbook, int sheetNum) {
		this.parser = parser;
		this.workbook = workbook;
		Sheet sheet = workbook.getSheetAt(Math.max(sheetNum, 0));
		this.max = sheet.getLastRowNum();
		this.iterator = sheet.iterator();
		this.preHeaders = Lists.newArrayList();
	}

	@Override
	public String getPreTitle(int rowNum, int cellNum) {
		if (TITLE == -1) {
			findTitle();
		}
		if (rowNum >= preHeaders.size() || rowNum < 0) {
			return null;
		}
		Row row = preHeaders.get(rowNum);
		if (row == null) {
			return null;
		}
		Cell cell = row.getCell(cellNum);
		if (cell == null) {
			return null;
		}
		return cell.getStringCellValue();
	}

	@Override
	public boolean findTitle() {
		return findTitle(-1);
	}

	@Override
	public boolean findTitle(int num) {
		if (TITLE != -1) {
			return true;
		}
		while (iterator.hasNext()) {
			Row row = iterator.next();
			if (num != -1 && row.getRowNum() > num) {
				break;
			}
			boolean match = parser.match(row);
			if (match) {
				TITLE = row.getRowNum();
				return true;
			} else {
				preHeaders.add(row);
			}
		}
		return false;
	}

	@Override
	public List<T> getData() {
		return readList(-1);
	}

	private List<T> readList(int length) {
		if (TITLE == -1 && !findTitle()) {
			throw new ExcelException("模版错误");
		}
		int counter = 0;
		List<T> fetch = new ArrayList<>(length > 0 ? length : max);
		while (iterator.hasNext()) {
			Row row = iterator.next();
			if (parser.isEmpty(row)) {
				continue;
			}
			Optional<T> convert = parser.convert(row);
			if (convert.isPresent()) {
				T t = convert.get();
				counter++;
				fetch.add(t);
			}
			if (length > 0 && counter == length) {
				break;
			}
		}
		if (!iterator.hasNext()) {
			close();
		}
		return fetch;
	}

	@Override
	public boolean hasNext() {
		return iterator.hasNext();
	}

	@Override
	public List fetchAllData() {
		return readList(-1);
	}

	@Override
	public List<T> fetchData(int length) {
		if (length <= 0) {
			throw new NotSupportedException("返回值集合长度应该是一个大于0的数");
		}
		return readList(length);
	}


	public void close() {
		try {
			workbook.close();
		} catch (IOException e) {
			log.error("workbook can not close ", e);
		}
	}
}
