package com.github.fnwib.databing.title;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class CellContent {

    private int    rowNum;
    private int    cellNum;
    private String text;

    public CellContent(int rowNum, int cellNum, String text) {
        this.rowNum = rowNum;
        this.cellNum = cellNum;
        this.text = text;
    }
}
