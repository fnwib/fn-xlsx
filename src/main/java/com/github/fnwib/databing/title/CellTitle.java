package com.github.fnwib.databing.title;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Objects;

@Getter
@EqualsAndHashCode
@ToString
public class CellTitle {
    private final Integer rowNum;
    private final Integer cellNum;
    private final String  text;
    private       boolean bind;

    public CellTitle(Integer rowNum, Integer cellNum, String text) {
        Objects.requireNonNull(rowNum);
        Objects.requireNonNull(cellNum);
        Objects.requireNonNull(text);
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
