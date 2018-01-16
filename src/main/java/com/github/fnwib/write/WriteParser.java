package com.github.fnwib.write;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.convert.ExcelConverter;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.PropertyException;
import com.github.fnwib.parse.Title;
import com.github.fnwib.reflect.Property;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class WriteParser<T> {

    private final Map<Method, Title> RULES = new HashMap<>();

    private Sheet sheet;

    private CellStyle cellStyle;

    public WriteParser(Map<Property, Title> rules) {
        initRules(rules);
    }

    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    public void setCellStyle(CellStyle defaultCellStyle) {
        this.cellStyle = defaultCellStyle;
    }

    private void initRules(Map<Property, Title> rules) {
        rules.forEach((property, title) -> {
            PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
            if (propertyDescriptor.getReadMethod() == null) {
                throw new PropertyException(propertyDescriptor.getName() + "没有标准的getter");
            } else {
                log.debug("property is '{}' , setter is '{}'", property.getName(), propertyDescriptor.getReadMethod().getName());
                RULES.put(propertyDescriptor.getReadMethod(), title);
            }
        });
    }

    public void convert(int rowNum, T element) {
        convert(this.sheet, rowNum, element);
    }

    public void convert(Sheet sheet, int rowNum, T element) {
        Row row = sheet.createRow(rowNum);
        if (element == null) {
            return;
        }
        Stream<CellText> stream = getCellTextStream(element);
        stream.forEach(cellText -> {
            Cell cell = row.createCell(cellText.getCellNum());
            cell.setCellStyle(cellStyle);
            cell.setCellValue(cellText.getText());
        });
    }

    private Stream<CellText> getCellTextStream(T element) {
        Stream.Builder<List<CellText>> builder = Stream.builder();
        RULES.forEach((method, title) -> {
            try {
                Object value = method.invoke(element);
                CellType cellType = title.getCellType();
                if (cellType.operation() != Operation.LINE_NUM) {
                    ExcelConverter<?> converter = title.getConverter();
                    List<CellText> cellTexts = converter.writeValue(value, title);
                    builder.add(cellTexts);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("error", e);
                throw new PropertyException(e);
            }
        });
        return builder.build().flatMap(List::stream).sorted(Comparator.comparing(CellText::getCellNum));
    }

    public void convert(int rowNum, List<T> elements, List<Integer> mergedRangeIndexes) {
        convert(this.sheet, rowNum, elements, mergedRangeIndexes);
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
                throw new ExcelException("列值不同不能合并单元格");
            }
            CellRangeAddress cellRangeAddress = new CellRangeAddress(rowNum, rowNum + elements.size() - 1,
                    mergedRangeIndex, mergedRangeIndex);
            sheet.addMergedRegion(cellRangeAddress);
        }
        for (List<CellText> cellTexts : cellTextMatrix.getMatrix()) {
            for (CellText cellText : cellTexts) {
                Row row = sheet.getRow(cellText.getRowNum());
                Cell cell = row.createCell(cellText.getCellNum());
                cell.setCellStyle(cellStyle);
                cell.setCellValue(cellText.getText());
            }
        }
    }


}
