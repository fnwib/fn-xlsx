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

    private String prefix;
    private String value;
    private String suffix;

    private boolean bind;

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

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setSuffix(String suffix) {
        this.suffix = suffix;
    }
}
