package com.github.fnwib.write;

import com.google.common.base.Objects;

public class CellText implements Cloneable {
    private int    rowNum;
    private int    cellNum;
    private String text;

    public CellText(int cellNum, String text) {
        this.cellNum = cellNum;
        this.text = text;
    }

    public CellText(int rowNum, int cellNum, String text) {
        this.rowNum = rowNum;
        this.cellNum = cellNum;
        this.text = text;
    }

    public CellText setRowNum(int rowNum) {
        this.rowNum = rowNum;
        return this;
    }

    public int getRowNum() {
        return rowNum;
    }

    public int getCellNum() {
        return cellNum;
    }

    public String getText() {
        return text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellText cellText = (CellText) o;
        return rowNum == cellText.rowNum &&
                cellNum == cellText.cellNum &&
                Objects.equal(text, cellText.text);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(rowNum, cellNum, text);
    }

    @Override
    public String toString() {
        return "CellText{" +
                "rowNum=" + rowNum +
                ", cellNum=" + cellNum +
                ", text='" + text + '\'' +
                '}';
    }
}
