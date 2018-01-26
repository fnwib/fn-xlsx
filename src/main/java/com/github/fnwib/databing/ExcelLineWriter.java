package com.github.fnwib.databing;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.write.CellText;
import com.github.fnwib.write.CellTextMatrix;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddress;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
public class ExcelLineWriter<T> implements LineWriter<T> {

    private Sheet              sheet;
    private CellStyle          cellStyle;
    private Set<PropertyToken> tokens;

    public ExcelLineWriter(Set<PropertyToken> tokens) {
        this.tokens = tokens;
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
        for (PropertyToken token : tokens) {
            WriteToken writeToken = token.getWriteToken();
            List<CellText> cellText = writeToken.getCellText(element);
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
