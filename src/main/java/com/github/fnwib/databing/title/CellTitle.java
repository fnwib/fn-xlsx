package com.github.fnwib.databing.title;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class CellTitle {
    private int    rowNum;
    private int    cellNum;
    private String text;

    public CellTitle(int cellNum, String text) {
        this.cellNum = cellNum;
        this.text = text;
    }

    public CellTitle(int rowNum, int cellNum, String text) {
        this.rowNum = rowNum;
        this.cellNum = cellNum;
        this.text = text;
    }

    public CellTitle setRowNum(int rowNum) {
        this.rowNum = rowNum;
        return this;
    }

}
