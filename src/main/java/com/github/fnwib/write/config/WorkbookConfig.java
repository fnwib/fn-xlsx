package com.github.fnwib.write.config;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.google.common.collect.Lists;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.List;

public class WorkbookConfig<T> {

    private static final Logger log = LoggerFactory.getLogger(WorkbookConfig.class);

    private final LineReader<T>     lineReader;
    private final ResultFileSetting resultFileSetting;
    private final TemplateSetting   templateSetting;
    private Integer titleRowNum;

    public WorkbookConfig(LineReader<T> lineReader,
                          ResultFileSetting resultFileSetting,
                          TemplateSetting templateSetting) {
        this.lineReader = lineReader;
        this.resultFileSetting = resultFileSetting;
        this.templateSetting = templateSetting;
    }


    public List<File> getResultFiles() {
        File resultFolder = resultFileSetting.getResultFolder();
        File[] files = resultFolder.listFiles();
        if (files == null) return Collections.emptyList();
        return Lists.newArrayList(files);
    }


    public LineReader<T> getLineReader() {
        return lineReader;
    }

    public ResultFileSetting getResultFileSetting() {
        return resultFileSetting;
    }

    public TemplateSetting getTemplateSetting() {
        return templateSetting;
    }

    private int findTitle() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(templateSetting.getTemplate());
            for (Sheet rows : workbook) {
                for (Row row : rows) {
                    boolean matched = lineReader.match(row);
                    if (matched) {
                        if (row.getRowNum() >= resultFileSetting.getMaxRowsCanWrite()) {
                            throw new SettingException("sheet可写最大行小于title所在行");
                        }
                        return row.getRowNum();
                    }
                }
            }
        } catch (IOException | InvalidFormatException e) {
            log.error("open workbook error ", e);
        }
        throw new ExcelException("模版错误");
    }

    public Integer getTitleRowNum() {
        if (titleRowNum == null) {
            titleRowNum = findTitle();
        }
        return titleRowNum;
    }

    public WorkbookBuilder<T> createWorkbookWrap() {
        if (titleRowNum == null) {
            titleRowNum = findTitle();
        }
        return new WorkbookBuilder<>(this, titleRowNum);
    }

}
