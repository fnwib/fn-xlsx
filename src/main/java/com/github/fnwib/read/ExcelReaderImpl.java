package com.github.fnwib.read;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.exception.ExcelException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ExcelReaderImpl<T> implements ExcelReader<T> {

    private static final Logger log = LoggerFactory.getLogger(ExcelReaderImpl.class);


    private int TITLE = -1;

    //记录TITLE前的数据
    private final Map<Integer, Row> PRE_DATA = new HashMap<>();

    private final LineReader<T> parser;

    private final Workbook workbook;
    private final Sheet    sheet;
    private final int      lastRowNum;
    private       int      currentRowNum;

    public ExcelReaderImpl(LineReader<T> parser, Workbook workbook, int sheetNum) {
        this.parser = parser;
        this.workbook = workbook;
        this.sheet = workbook.getSheetAt(Math.max(sheetNum, 0));
        this.lastRowNum = sheet.getLastRowNum();
    }

    @Override
    public String getPreTitle(int rowNum, int cellNum) {
        if (TITLE == -1) {
            findTitle();
        }
        Row row = PRE_DATA.get(rowNum);
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
        for (Row row : sheet) {
            if (num != -1 && row.getRowNum() > num) {
                break;
            }
            boolean match = parser.match(row);
            if (match) {
                TITLE = row.getRowNum();
                return true;
            } else {
                PRE_DATA.put(row.getRowNum(), row);
            }
        }
        return false;
    }

    @Override
    public List<T> getData() throws ExcelException {
        return readList(sheet.getLastRowNum());
    }


    private List<T> readList(int length) {
        if (TITLE == -1 && !findTitle()) {
            throw new ExcelException("模版错误");
        }
        AtomicInteger counter = new AtomicInteger();
        List<T> fetch = new ArrayList<>(length);
        for (Row row : sheet) {
            currentRowNum = row.getRowNum();
            if (row.getRowNum() <= TITLE || parser.isEmpty(row)) {
                continue;
            }
            Optional<T> convert = parser.convert(row);
            if (convert.isPresent()) {
                T t = convert.get();
                counter.addAndGet(1);
                fetch.add(t);
            }
            if (counter.get() == length) {
                break;
            }
        }
        if (!hasNext()) {
            close();
        }
        return fetch;
    }

    @Override
    public boolean hasNext() {
        return currentRowNum < lastRowNum;
    }

    @Override
    public List fetchData() {
        return readList(sheet.getLastRowNum());
    }

    @Override
    public List<T> fetchData(int length) {
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
