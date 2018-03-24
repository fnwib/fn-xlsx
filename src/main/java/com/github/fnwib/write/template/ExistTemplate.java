package com.github.fnwib.write.template;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.write.CellText;
import com.github.fnwib.write.config.ResultFileSetting;
import com.github.fnwib.write.config.TemplateSetting;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class ExistTemplate<T> extends Template<T> {
    private static final Logger log = LoggerFactory.getLogger(ExistTemplate.class);

    private File template;
    private int  titleRowNum;

    public ExistTemplate(LineReader<T> lineReader,
                         TemplateSetting templateSetting,
                         ResultFileSetting resultFileSetting) {
        super(lineReader, templateSetting, resultFileSetting);
        this.template = buildWorkbook();
    }

    @Override
    public SXSSFWorkbook getWriteWorkbook() throws IOException {
        FileInputStream inputStream = FileUtils.openInputStream(template);
        this.workbook = new XSSFWorkbook(inputStream);
        return new SXSSFWorkbook(workbook);
    }

    @Override
    public int getTiltRowNum() {
        return titleRowNum;
    }

    private Integer findTitle(Workbook workbook) {
        for (Sheet rows : workbook) {
            for (Row row : rows) {
                boolean matched = lineReader.match(row);
                if (matched) {
                    if (row.getRowNum() >= resultFileSetting.getMaxRowsCanWrite()) {
                        throw new SettingException("sheet可写最大行小于title所在行");
                    }
                    titleRowNum = row.getRowNum();
                    return row.getRowNum();
                }
            }
        }
        throw new ExcelException("模版错误");
    }


    private File buildWorkbook() {
        try {
            File template = templateSetting.getTemplate();
            Workbook workbook = new XSSFWorkbook(FileUtils.openInputStream(template));
            Integer title = findTitle(workbook);
            buildSheet(workbook, 0, title);

            File emptyFile = resultFileSetting.getEmptyFile();
            workbook.write(FileUtils.openOutputStream(emptyFile));
            return emptyFile;
        } catch (IOException e) {
            log.error("open workbook error ", e);
            throw new ExcelException("模版错误");
        }
    }

    void buildSheet(Workbook workbook, int sheetIndex, int titleRowNum) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        if (StringUtils.isNotBlank(templateSetting.getSheetName())) {
            workbook.setSheetName(sheetIndex, templateSetting.getSheetName());
        }
        if (templateSetting.changed()) {
            List<CellText> cellTexts = templateSetting.getCellTexts();
            for (CellText cellText : cellTexts) {
                if (cellText.getRowNum() >= titleRowNum) {
                    throw new SettingException("只能修改title上方的数据, " + cellText);
                }
                Row row = getRow(sheet, cellText.getRowNum());
                Cell cell = getCell(row, cellText.getCellNum());
                cell.setCellValue(cellText.getText());
            }
            Row row = getRow(sheet, titleRowNum);
            int cellNum = row.getLastCellNum();
            CellStyle cellStyle = row.getCell(cellNum - 1).getCellStyle();
            for (String title : templateSetting.getAddLastTitles()) {
                Cell cell = row.createCell(cellNum++);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(title);
            }
            lineReader.match(row);
        }
    }



}