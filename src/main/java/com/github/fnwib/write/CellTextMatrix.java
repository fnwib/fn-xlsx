package com.github.fnwib.write;

import com.github.fnwib.exception.ExcelException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
@Deprecated
public class CellTextMatrix {

    private final List<List<CellText>> matrix;

    public CellTextMatrix(int initialCapacity) {
        this.matrix = new ArrayList<>(initialCapacity);
    }

    public void addMatrixRow(List<CellText> matrixRow) {
        checkMatrix(matrixRow);
        this.matrix.add(matrixRow);
    }

    public void addMatrixRow(Stream<CellText> matrixRow) {
        List<CellText> cellTexts = matrixRow.collect(Collectors.toList());
        this.addMatrixRow(cellTexts);
    }

    public void checkMatrix(List<CellText> matrixRow) {
        if (!matrix.isEmpty()) {
            List<CellText> firstRow = matrix.get(0);
            if (matrixRow.isEmpty()) {
                throw new ExcelException("矩阵行不能为空");
            }
            if (firstRow.size() != matrixRow.size()) {
                throw new ExcelException("矩阵行元素数量不一致");
            }
            for (int i = 0; i < matrixRow.size(); i++) {
                CellText first = firstRow.get(i);
                CellText curr = matrixRow.get(i);
                if (first == null || curr == null) {
                    throw new ExcelException("矩阵元素不能为null");
                }
                if (first.getCellNum() != curr.getCellNum()) {
                    throw new ExcelException("矩阵列数据没有对齐");
                }
            }
        }
    }

    public boolean isSameMatrixColumn(int column) {
        if (matrix.isEmpty() || matrix.get(0).size() <= column) {
            return false;
        }
        return matrix.stream().map(cellTexts -> cellTexts.get(column)).map(CellText::getText).distinct().count() == 1L;
    }

    public Stream<CellText> getMatrixColumn(int column) {
        if (matrix.isEmpty() || matrix.get(0).size() <= column) {
            return Stream.empty();
        }
        return matrix.stream().map(cellTexts -> cellTexts.get(column));
    }

    public Stream<CellText> getMatrixRow(int row) {
        if (matrix.isEmpty() || matrix.size() <= row) {
            return Stream.empty();
        }
        return matrix.get(row).stream();
    }


    public List<List<CellText>> getMatrix() {
        return matrix;
    }
}
