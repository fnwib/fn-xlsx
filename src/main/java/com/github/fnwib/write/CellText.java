package com.github.fnwib.write;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class CellText {
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

}
