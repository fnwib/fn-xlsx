package com.github.fnwib.write.config;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.parse.Parser;
import com.github.fnwib.write.WriteParser;
import com.google.common.collect.Queues;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

@Slf4j
@Getter
public class WorkbookConfig<T> {

    private final Parser<T>           parser;
    private final ResultFileSetting   resultFileSetting;
    private final TemplateSetting     templateSetting;
    private final ExportType          exportType;
    private final Queue<WorkbookWrap> workbookWraps;


    private Integer titleRowNum;

    public WorkbookConfig(Parser<T> parser,
                          ExportType exportType,
                          ResultFileSetting resultFileSetting,
                          TemplateSetting templateSetting) {
        this.parser = parser;
        this.exportType = exportType;
        this.resultFileSetting = resultFileSetting;
        this.templateSetting = templateSetting;
        this.workbookWraps = Queues.newArrayDeque();
    }

    public WriteParser<T> getWriteParser() {
        WriteParser<T> writeParser = parser.createWriteParser();
        if (templateSetting.isUseDefaultCellStyle()) {
            CellStyle cellStyle = workbookWraps.peek().getCellStyle();
            writeParser.setCellStyle(cellStyle);
        }
        return writeParser;
    }

    public int getTitleRowNum() {
        return titleRowNum;
    }

    private void pushNewWorkbookWrap() {
        try {
            File template = resultFileSetting.copyFile(templateSetting.getTemplate());
            XSSFWorkbook workbook = new XSSFWorkbook(FileUtils.openInputStream(template));
            if (templateSetting.updateTitle()) {
                XSSFSheet sheet = workbook.getSheetAt(0);
                XSSFRow row = sheet.getRow(this.findTitle(sheet));
                int cellNum = row.getLastCellNum();
                XSSFCellStyle cellStyle = row.getCell(cellNum - 1).getCellStyle();
                for (String title : templateSetting.getAddLastTitles()) {
                    XSSFCell cell = row.createCell(cellNum++);
                    cell.setCellStyle(cellStyle);
                    cell.setCellValue(title);
                }
            }
            String sheetName = templateSetting.getSheetName(exportType);
            if (StringUtils.isNotBlank(sheetName)) {
                workbook.setSheetName(0, sheetName);
            }
            this.findTitle(workbook.getSheetAt(0));
            workbookWraps.add(new WorkbookWrap(template, workbook));
        } catch (IOException e) {
            throw new SettingException(e);
        }
    }

    private int findTitle(Sheet sheet) {
        for (Row row : sheet) {
            boolean matched = parser.match(row);
            if (matched) {
                if (row.getRowNum() >= resultFileSetting.getMaxRowsCanWrite()) {
                    throw new SettingException("sheet可写最大行小于title所在行");
                }
                titleRowNum = row.getRowNum();
                return row.getRowNum();
            }
        }
        throw new ExcelException("模版错误");
    }

    public synchronized Sheet getNextSheet() {
        if (exportType == ExportType.SingleSheet) {
            if (!workbookWraps.isEmpty()) {
                write();
            }
            pushNewWorkbookWrap();
            SXSSFWorkbook workbook = workbookWraps.peek().getWriteWorkbooks();
            SXSSFSheet sheet = workbook.getSheetAt(0);
            return sheet;
        } else if (exportType == ExportType.MultiSheet) {
            throw new NotSupportedException("暂时不支持导出类型, " + exportType.name());
        } else {
            throw new NotSupportedException("不支持导出类型, " + exportType.name());
        }
    }

    public CellStyle getCellStyle() {
        return workbookWraps.peek().getCellStyle();
    }



    public void write() {
        workbookWraps.poll().writeTo(resultFileSetting.getNextResultFile());
    }

    public List<File> getResultFiles() {
        File resultFolder = resultFileSetting.getResultFolder();
        File[] files = resultFolder.listFiles();
        return Arrays.asList(files);
    }

}
