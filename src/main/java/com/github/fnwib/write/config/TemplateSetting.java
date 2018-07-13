package com.github.fnwib.write.config;

import com.github.fnwib.exception.SettingException;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.List;

public class TemplateSetting {
    private static final int step = 500000;
    @Getter
    private int            maxRowsCanWrite;
    @Getter
    @Setter
    private File           template;
    @Getter
    @Setter
    private String         sheetName;
    @Getter
    @Setter
    private boolean        useDefaultCellStyle;
    @Getter
    private List<String>   lastTitles;
    @Getter
    private List<CellText> cellTexts;

    public TemplateSetting(int maxRowsCanWrite) {
        if (maxRowsCanWrite <= 0) {
            throw new SettingException("Sheet可写入最大行不能小于等于0");
        }
        this.maxRowsCanWrite = maxRowsCanWrite;
        this.lastTitles = Lists.newArrayList();
        this.cellTexts = Lists.newArrayList();
    }

    public TemplateSetting() {
        this(step);
    }


    public boolean gt(int rowNum) {
        return rowNum > maxRowsCanWrite;
    }

    public boolean changed() {
        return !lastTitles.isEmpty() || !cellTexts.isEmpty();
    }

    public void useDefaultCellStyle() {
        this.useDefaultCellStyle = true;
    }

    public void addLastTitles(List<String> lastTitles) {
        for (String lastTitle : lastTitles) {
            this.lastTitles.add(lastTitle);
        }
    }

    public void addCellTexts(List<CellText> cellTexts) {
        for (CellText cellText : cellTexts) {
            this.cellTexts.add(cellText);
        }
    }

    public void addCellText(CellText cellText) {
        this.cellTexts.add(cellText);
    }
}
