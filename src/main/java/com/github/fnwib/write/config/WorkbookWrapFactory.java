package com.github.fnwib.write.config;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class WorkbookWrapFactory<T> {

    private static final Logger log = LoggerFactory.getLogger(WorkbookWrapFactory.class);


    private final WorkbookConfig<T> workbookConfig;
    private final Integer           titleRowNum;

    public WorkbookWrapFactory(WorkbookConfig<T> workbookConfig) {
        this.workbookConfig = workbookConfig;
        this.titleRowNum = findTitle();
    }

    public Integer getTitleRowNum() {
        return titleRowNum;
    }

    private int findTitle() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(workbookConfig.getTemplateSetting().getTemplate());
            for (Sheet rows : workbook) {
                for (Row row : rows) {
                    boolean matched = workbookConfig.getLineReader().match(row);
                    if (matched) {
                        if (row.getRowNum() >= workbookConfig.getResultFileSetting().getMaxRowsCanWrite()) {
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
        return new WorkbookWrap<>(workbookConfig, titleRowNum);
    }

}
