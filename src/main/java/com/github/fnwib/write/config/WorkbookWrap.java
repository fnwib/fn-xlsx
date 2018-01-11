package com.github.fnwib.write.config;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.parse.Parser;
import com.github.fnwib.write.WriteParser;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Getter
public class WorkbookWrap<T> {

    private static final int defaultSheetIndex = 0;
    private final Parser<T>         parser;
    private final File              templateFile;
    private final XSSFWorkbook      templateWorkbook;
    private final SXSSFWorkbook     writeWorkbooks;
    private final ResultFileSetting resultFileSetting;
    private final TemplateSetting   templateSetting;
    private final ExportType        exportType;

    private final int titleRowNum;

    private boolean written;

    public WorkbookWrap(Parser<T> parser,
                        ExportType exportType,
                        ResultFileSetting resultFileSetting,
                        TemplateSetting templateSetting,
                        int titleRowNum) {
        this.parser = parser;
        this.exportType = exportType;
        this.resultFileSetting = resultFileSetting;
        this.templateSetting = templateSetting;
        this.templateFile = resultFileSetting.copyFile(templateSetting.getTemplate());
        this.titleRowNum = titleRowNum;
        this.templateWorkbook = buildWorkbook();
        this.writeWorkbooks = new SXSSFWorkbook(templateWorkbook);
        this.written = false;
    }

    public WriteParser<T> getWriteParser() {
        if (written) {
            throw new ExcelException("excel已经写入文件");
        }
        WriteParser<T> writeParser = parser.createWriteParser();
        if (templateSetting.isUseDefaultCellStyle()) {
            writeParser.setCellStyle(getCellStyle());
        }
        writeParser.setSheet(getNextSheet());
        return writeParser;
    }

    private Sheet getNextSheet() {
        if (exportType == ExportType.SingleSheet) {
            written = true;
            return writeWorkbooks.getSheetAt(defaultSheetIndex);
        } else {
            throw new NotSupportedException("不支持导出类型, " + exportType.name());
        }
    }

    private XSSFWorkbook buildWorkbook() {
        try {
            XSSFWorkbook workbook = new XSSFWorkbook(FileUtils.openInputStream(templateFile));
            buildSheet(workbook, defaultSheetIndex);
            if (StringUtils.isNotBlank(templateSetting.getSheetName())) {
                workbook.setSheetName(defaultSheetIndex, templateSetting.getSheetName());
            }
            return workbook;
        } catch (IOException e) {
            log.error("build workbook error", e);
            throw new ExcelException(e);
        }
    }

    private Sheet buildSheet(XSSFWorkbook workbook, int sheetIndex) {
        XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
        if (templateSetting.updateTitle()) {
            XSSFRow row = sheet.getRow(titleRowNum);
            int cellNum = row.getLastCellNum();
            XSSFCellStyle cellStyle = row.getCell(cellNum - 1).getCellStyle();
            for (String title : templateSetting.getAddLastTitles()) {
                XSSFCell cell = row.createCell(cellNum++);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(title);
            }
            parser.match(row);
        }
        return sheet;
    }

    private CellStyle getCellStyle() {
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

    public void write() {
        try (OutputStream outputStream = new FileOutputStream(resultFileSetting.getNextResultFile())) {
            writeWorkbooks.write(outputStream);
            writeWorkbooks.close();
            templateWorkbook.close();
            FileUtils.forceDelete(templateFile);
        } catch (IOException e) {
            throw new SettingException(e);
        }
    }

}
