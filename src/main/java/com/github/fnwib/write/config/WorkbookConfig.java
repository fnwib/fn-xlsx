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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;

@Slf4j
@Getter
public class WorkbookConfig<T> {

    private final Parser<T>            parser;
    private final ResultFileSetting    resultFileSetting;
    private final TemplateSetting      templateSetting;
    private final ExportType           exportType;
    private final File                 templateFile;
    private final XSSFWorkbook         templateWorkbook;
    private final Queue<SXSSFWorkbook> writeWorkbooks;

    private Integer titleRowNum;

    public WorkbookConfig(Parser<T> parser,
                          ExportType exportType,
                          ResultFileSetting resultFileSetting,
                          TemplateSetting templateSetting) {
        this.parser = parser;
        this.exportType = exportType;
        this.resultFileSetting = resultFileSetting;
        this.templateSetting = templateSetting;
        this.templateFile = resultFileSetting.copyFile(templateSetting.getTemplate());
        this.templateWorkbook = buildWorkbook();
        this.writeWorkbooks = Queues.newArrayDeque();
    }

    public CellStyle getCellStyle() {
        CellStyle cellStyle = templateWorkbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("text"));
        Font font2 = templateWorkbook.createFont();
        font2.setFontName("Arial");
        font2.setFontHeightInPoints((short) 10);
        cellStyle.setFont(font2);
        return cellStyle;
    }


    public WriteParser<T> getWriteParser() {
        WriteParser<T> writeParser = parser.createWriteParser();
        if (templateSetting.isUseDefaultCellStyle()) {
            CellStyle cellStyle = getCellStyle();
            writeParser.defaultCellStyle(cellStyle);
        }
        return writeParser;
    }

    public int getTitleRowNum() {
        return titleRowNum;
    }

    private XSSFWorkbook buildWorkbook() {
        File template = templateSetting.getTemplate();
        try {
            File newTemplate = resultFileSetting.copyFile(template);
            XSSFWorkbook workbook = new XSSFWorkbook(FileUtils.openInputStream(newTemplate));
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
            this.findTitle(workbook.getSheetAt(0));
            return workbook;
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

    /**
     * 第一个Sheet会根据配置初始化
     *
     * @return
     */
    private void pushDuplicateWorkBook() {
        String sheetName = templateSetting.getSheetName(exportType);
        if (StringUtils.isNotBlank(sheetName)) {
            templateWorkbook.setSheetName(0, sheetName);
        }
        writeWorkbooks.add(new SXSSFWorkbook(templateWorkbook));
    }

    public synchronized Sheet getNextSheet() {
        if (exportType == ExportType.SingleSheet) {
            if (!writeWorkbooks.isEmpty()) {
                write();
            }
            pushDuplicateWorkBook();
            SXSSFWorkbook workbook = writeWorkbooks.peek();
            SXSSFSheet sheet = workbook.getSheetAt(0);
            return sheet;
        } else if (exportType == ExportType.MultiSheet) {
            throw new NotSupportedException("暂时不支持导出类型, " + exportType.name());
        } else {
            throw new NotSupportedException("不支持导出类型, " + exportType.name());
        }
    }


    public void write() {
        try (OutputStream outputStream = new FileOutputStream(resultFileSetting.getNextResultFile())) {
            writeWorkbooks.poll().write(outputStream);
        } catch (IOException e) {
            throw new SettingException(e);
        }
    }

    public List<File> getResultFiles() {
        File resultFolder = resultFileSetting.getResultFolder();
        File[] files = resultFolder.listFiles();
        return Arrays.asList(files);
    }

    public void close() {
        try {
            templateWorkbook.close();
            FileUtils.forceDelete(templateFile);
        } catch (IOException e) {
            log.error("closed ", e);
        }
    }
}
