package com.github.fnwib.read;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.operation.Operator;
import com.github.fnwib.operation.Title;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelReaderImpl<T> implements ExcelReader<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelReaderImpl.class);

    private int TITLE = -1;

    private Map<Method, Title> RULE = new HashMap<>();

    //记录TITLE前的数据
    private final Map<Integer, Row> PRE_DATA = new HashMap<>();

    private final Operator<T> operator;

    private final Workbook workbook;

    private final int sheetNum;

    public ExcelReaderImpl(Operator<T> operator, Workbook workbook, int sheetNum) {
        this.operator = operator;
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
            boolean match = operator.match(row);
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
        if (RULE == null) {
            return new ArrayList<>(0);
        }
        List<T> list = new ArrayList<>(sheet.getLastRowNum());
        for (Row row : sheet) {
            if (row.getRowNum() <= TITLE || this.isEmpty(row)) {
                continue;
            }
            T t = operator.convert(row);
            if (t != null) {
                list.add(t);
            }
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
