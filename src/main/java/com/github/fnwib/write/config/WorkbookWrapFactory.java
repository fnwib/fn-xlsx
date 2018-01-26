package com.github.fnwib.write.config;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

@Slf4j
public class WorkbookWrapFactory<T> {

    private final WorkbookConfig<T> workbookConfig;
    @Getter
    private final Integer           titleRowNum;

    public WorkbookWrapFactory(WorkbookConfig<T> workbookConfig) {
        this.workbookConfig = workbookConfig;
        this.titleRowNum = findTitle();
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
