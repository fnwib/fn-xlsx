package com.github.fnwib.write.config;

import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;

public class TemplateSetting<T> {

    private File           template;
    private String         sheetName;
    private boolean        useDefaultCellStyle;
    private List<String>   lastTitles;
    private List<CellText> cellTexts;

    public TemplateSetting() {
        this.lastTitles = Lists.newArrayList();
        this.cellTexts = Lists.newArrayList();
    }

    public File getTemplate() {
        return template;
    }

    public boolean changed() {
        return !lastTitles.isEmpty() || !cellTexts.isEmpty();
    }


    public void setTemplate(File template) {
        this.template = template;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public boolean isUseDefaultCellStyle() {
        return useDefaultCellStyle;
    }

    public void useDefaultCellStyle() {
        this.useDefaultCellStyle = true;
    }

    public List<String> getAddLastTitles() {
        return lastTitles;
    }

    public void addLastTitles(List<String> lastTitles) {
        for (String lastTitle : lastTitles) {
            this.lastTitles.add(lastTitle);
        }
    }

    public List<CellText> getCellTexts() {
        return cellTexts;
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
