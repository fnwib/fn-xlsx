package com.github.fnwib.write.config;

import com.github.fnwib.databing.LineWriter;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.write.CellText;
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
import java.util.List;

@Slf4j
@Getter
public class WorkbookWrap<T> {

    private static final int defaultSheetIndex = 0;

    private final LineWriter<T>     lineWriter;
    private final File              templateFile;
    private final XSSFWorkbook      templateWorkbook;
    private final SXSSFWorkbook     writeWorkbooks;
    private final ResultFileSetting resultFileSetting;
    private final TemplateSetting   templateSetting;
    private final WorkbookConfig<T> workbookConfig;

    private final int titleRowNum;

    private boolean written;

    WorkbookWrap(WorkbookConfig<T> workbookConfig, int titleRowNum) {
        this.workbookConfig = workbookConfig;
        this.lineWriter = workbookConfig.getLineReader().getLineWriter();
        this.resultFileSetting = workbookConfig.getResultFileSetting();
        this.templateSetting = workbookConfig.getTemplateSetting();
        this.templateFile = resultFileSetting.copyFile(templateSetting.getTemplate());
        this.titleRowNum = titleRowNum;
        this.templateWorkbook = buildWorkbook();
        this.writeWorkbooks = new SXSSFWorkbook(templateWorkbook);
        this.written = false;
    }

    public LineWriter<T> getWriteParser() {
        if (written) {
            throw new ExcelException("excel已经写入文件");
        }
        if (templateSetting.isUseDefaultCellStyle()) {
            lineWriter.setCellStyle(getCellStyle());
        }
        lineWriter.setSheet(getNextSheet());
        return lineWriter;
    }

    private Sheet getNextSheet() {
        written = true;
        return writeWorkbooks.getSheetAt(defaultSheetIndex);
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

    private void buildSheet(XSSFWorkbook workbook, int sheetIndex) {
        XSSFSheet sheet = workbook.getSheetAt(sheetIndex);
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
            XSSFRow row = sheet.getRow(titleRowNum);
            int cellNum = row.getLastCellNum();
            XSSFCellStyle cellStyle = row.getCell(cellNum - 1).getCellStyle();
            for (String title : templateSetting.getAddLastTitles()) {
                XSSFCell cell = row.createCell(cellNum++);
                cell.setCellStyle(cellStyle);
                cell.setCellValue(title);
            }
            workbookConfig.getLineReader().match(row);
        }
    }

    private Row getRow(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        return row == null ? sheet.createRow(rowNum) : row;
    }

    private Cell getCell(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        return cell == null ? row.createCell(cellNum) : cell;
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
