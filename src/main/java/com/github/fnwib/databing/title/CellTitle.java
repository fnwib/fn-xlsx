package com.github.fnwib.databing.title;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@EqualsAndHashCode
@ToString
public class CellTitle {
    private final int     rowNum;
    private final int     cellNum;
    private final String  text;
    private       boolean bind;

    public CellTitle(int rowNum, int cellNum, String text) {
        this.rowNum = rowNum;
        this.cellNum = cellNum;
        this.text = text;
        this.bind = false;
    }

    public boolean isBind() {
        return bind;
    }

    public void bind() {
        this.bind = true;
    }
}
