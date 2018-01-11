package com.github.fnwib.write.config;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.parse.Parser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

@Slf4j

public class WorkbookWrapFactory<T> {

    private final Parser<T>         parser;
    private final ResultFileSetting resultFileSetting;
    private final TemplateSetting   templateSetting;
    private final ExportType        exportType;
    @Getter
    private final Integer           titleRowNum;

    public WorkbookWrapFactory(WorkbookConfig<T> workbookConfig) {
        this.exportType = workbookConfig.getExportType();
        this.resultFileSetting = workbookConfig.getResultFileSetting();
        this.templateSetting = workbookConfig.getTemplateSetting();
        this.parser = workbookConfig.getParser();
        this.titleRowNum = findTitle();
    }

    private int findTitle() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(templateSetting.getTemplate());
            for (Sheet rows : workbook) {
                for (Row row : rows) {
                    boolean matched = parser.match(row);
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

    public WorkbookWrap<T> createWorkbookWrap() {
        return new WorkbookWrap<>(parser, exportType, resultFileSetting, templateSetting, titleRowNum);
    }

}
