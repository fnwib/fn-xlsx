package com.github.fnwib.databing;

import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.write.CellText;
import com.github.fnwib.write.CellTextMatrix;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class LineWriterForExcel<T> implements LineWriter<T> {

    private static final Logger log = LoggerFactory.getLogger(LineWriterForExcel.class);

    private Sheet                  sheet;
    private CellStyle              cellStyle;
    private Set<PropertyConverter> converters;

    public LineWriterForExcel(Set<PropertyConverter> converters) {
        this.converters = converters;
    }

    @Override
    public void setSheet(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void setCellStyle(CellStyle defaultCellStyle) {
        this.cellStyle = defaultCellStyle;
    }

    private Stream<CellText> getCellTextStream(T element) {
        Stream.Builder<List<CellText>> builder = Stream.builder();
        for (PropertyConverter converter : converters) {
            List<CellText> cellText = converter.getCellText(element);
            builder.add(cellText);
        }
        return builder.build().flatMap(List::stream).sorted(Comparator.comparing(CellText::getCellNum));
    }

    @Override
    public void convert(int rowNum, T element) {
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

    @Override
    public void convert(int rowNum, List<T> elements, List<Integer> mergedRangeIndexes) {
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
                log.error("-> 列值不同不能合并单元格 cell index is [{}]", mergedRangeIndex);
                for (T element : elements) {
                    log.error(" -> value [{}]", element);
                }
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
