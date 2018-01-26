package com.github.fnwib.read;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.exception.ExcelException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
public class ExcelReaderImpl2<T> implements ExcelReader<T> {

    private int TITLE = -1;

    //记录TITLE前的数据
    private final Map<Integer, Row> PRE_DATA = new HashMap<>();

    private final LineReader<T> parser;

    private final Workbook workbook;

    private final int sheetNum;

    public ExcelReaderImpl2(LineReader<T> parser, Workbook workbook, int sheetNum) {
        this.parser = parser;
        this.workbook = workbook;
        this.sheetNum = sheetNum;
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
        Sheet sheet = workbook.getSheetAt(sheetNum);
        for (Row row : sheet) {
            if (num != -1 && row.getRowNum() > num) {
                break;
            }
            if (this.isEmpty(row)) {
                continue;
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
        Sheet sheet = workbook.getSheetAt(sheetNum);
        if (TITLE == -1 && !findTitle()) {
            throw new ExcelException("模版错误");
        }
        List<T> list = new ArrayList<>(sheet.getLastRowNum());
        for (Row row : sheet) {
            if (row.getRowNum() <= TITLE || this.isEmpty(row)) {
                continue;
            }
            T t = parser.read(row);
            if (t != null) {
                list.add(t);
            }
        }
        try {
            workbook.close();
        } catch (IOException e) {
            log.error("reader workbook  close ", e);
        }
        return list;
    }

    public boolean isEmpty(Row row) {
        for (Cell cell : row) {
            if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
                return false;
            }
        }
        return true;
    }


}
