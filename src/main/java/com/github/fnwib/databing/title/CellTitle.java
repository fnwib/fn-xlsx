package com.github.fnwib.databing.title;

import com.github.fnwib.write.CellText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

public class CellTitle implements Cloneable {

    private static final Logger log = LoggerFactory.getLogger(CellTitle.class);

    private static final String EMPTY_TEXT = "";

    private final Integer rowNum;
    private final Integer cellNum;
    private final String  text;

    private String prefix;
    private String value;
    private String suffix;

    private boolean bind;

    CellTitle(Integer rowNum, Integer cellNum, String text) {
        Objects.requireNonNull(rowNum);
        Objects.requireNonNull(cellNum);
        Objects.requireNonNull(text);
        this.rowNum = rowNum;
        this.cellNum = cellNum;
        this.text = text;
        this.bind = false;
    }

    public CellText getEmptyCellText() {
        return new CellText(cellNum, EMPTY_TEXT);
    }

    public Integer getRowNum() {
        return rowNum;
    }

    public Integer getCellNum() {
        return cellNum;
    }

    public String getText() {
        return text;
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

    @Override
    public CellTitle clone() {
        try {
            super.clone();
        } catch (CloneNotSupportedException e) {
            log.error("-> clone error", e);
        }
        CellTitle title = new CellTitle(rowNum, cellNum, text);
        title.setPrefix(prefix);
        title.setValue(value);
        title.setSuffix(suffix);
        if (bind) title.bind();
        return title;

    }

    public Sequence getSequence() {
        return new Sequence(value);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellTitle title = (CellTitle) o;
        return bind == title.bind &&
                com.google.common.base.Objects.equal(rowNum, title.rowNum) &&
                com.google.common.base.Objects.equal(cellNum, title.cellNum) &&
                com.google.common.base.Objects.equal(text, title.text) &&
                com.google.common.base.Objects.equal(prefix, title.prefix) &&
                com.google.common.base.Objects.equal(value, title.value) &&
                com.google.common.base.Objects.equal(suffix, title.suffix);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(rowNum, cellNum, text, prefix, value, suffix, bind);
    }

    @Override
    public String toString() {
        return "CellTitle{" +
                "rowNum=" + rowNum +
                ", cellNum=" + cellNum +
                ", text='" + text + '\'' +
                ", prefix='" + prefix + '\'' +
                ", value='" + value + '\'' +
                ", suffix='" + suffix + '\'' +
                ", bind=" + bind +
                '}';
    }
}
