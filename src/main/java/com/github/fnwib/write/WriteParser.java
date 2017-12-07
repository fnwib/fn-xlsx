package com.github.fnwib.write;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.convert.ExcelConverter;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.parse.Title;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WriteParser<T> {

    private Class<T> entityClass;

    private final Map<Method, Title> RULES = new HashMap<>();

    private CellStyle defaultCellStyle;

    public WriteParser(Class<T> entityClass, Map<PropertyDescriptor, Title> rules) {
        this.entityClass = entityClass;
        try {
            initRules(rules);
        } catch (IntrospectionException e) {
            throw new ExcelException(e);
        }

    }

    public WriteParser defaultCellStyle(CellStyle defaultCellStyle) {
        this.defaultCellStyle = defaultCellStyle;
        return this;
    }

    private void initRules(Map<PropertyDescriptor, Title> rules) throws IntrospectionException {
        rules.forEach((propertyDescriptor, title) -> RULES.put(propertyDescriptor.getReadMethod(), title));
    }

    public void convert(Sheet sheet, int rowNum, T element) {
        Row row = sheet.createRow(rowNum);
        Stream<CellText> stream = getCellTextStream(element);
        stream.forEach(cellText -> {
            Cell cell = row.createCell(cellText.getCellNum());
            cell.setCellStyle(defaultCellStyle);
            cell.setCellValue(cellText.getText());
        });
    }

    private Stream<CellText> getCellTextStream(T element) {
        Stream.Builder<List<CellText>> builder = Stream.builder();
        RULES.forEach((method, title) -> {
            try {
                Object value = method.invoke(element);
                CellType cellType = title.getCellType();
                if (cellType.type() == Operation.DEFAULT) {
                    ExcelConverter<?> converter = title.getConverter();
                    List<CellText> cellTexts = converter.writeValue(value, title);
                    builder.add(cellTexts);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        });
        return builder.build().flatMap(List::stream).sorted(Comparator.comparing(CellText::getCellNum));
    }


    public void convert(Sheet sheet, int rowNum, List<T> elements, List<Integer> mergedRangeIndexes) {
        CellTextMatrix cellTextMatrix = new CellTextMatrix(elements.size());
        int tempRowNum = rowNum;
        for (T element : elements) {
            List<CellText> list = getCellTextStream(element).collect(Collectors.toList());
            for (CellText cellText : list) {
                cellText.setRowNum(tempRowNum);
            }
            cellTextMatrix.addMatrixRow(list);
            sheet.createRow(tempRowNum);
            tempRowNum++;
        }
        for (Integer mergedRangeIndex : mergedRangeIndexes) {
            boolean sameMatrixColumn = cellTextMatrix.isSameMatrixColumn(mergedRangeIndex);
            if (!sameMatrixColumn) {
                throw new ExcelException("元素值不同不能合并单元格");
            }
            CellRangeAddress cellRangeAddress = new CellRangeAddress(rowNum, rowNum + elements.size() - 1,
                    mergedRangeIndex, mergedRangeIndex);
            sheet.addMergedRegion(cellRangeAddress);
        }

        for (Integer mergedRangeIndex : mergedRangeIndexes) {
            Stream<CellText> matrixColumn = cellTextMatrix.getMatrixColumn(mergedRangeIndex);
            Optional<CellText> first = matrixColumn.findFirst();
            CellText cellText = first.get();
            Row row = sheet.getRow(cellText.getRowNum());
            Cell cell = row.createCell(cellText.getCellNum());
            cell.setCellStyle(defaultCellStyle);
            cell.setCellValue(cellText.getText());
        }
        for (List<CellText> cellTexts : cellTextMatrix.getMatrix()) {
            for (CellText cellText : cellTexts) {
                if (mergedRangeIndexes.contains(cellText.getCellNum())) {
                    continue;
                }
                Row row = sheet.getRow(cellText.getRowNum());
                Cell cell = row.createCell(cellText.getCellNum());
                cell.setCellStyle(defaultCellStyle);
                cell.setCellValue(cellText.getText());
            }
        }
    }


}
