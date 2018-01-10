package com.github.fnwib.write.config;

import com.github.fnwib.exception.SettingException;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

@Slf4j
@Getter
public class WorkbookWrap {

    private final File          templateFile;
    private final XSSFWorkbook  templateWorkbook;
    private final SXSSFWorkbook writeWorkbooks;

    public WorkbookWrap(File templateFile, XSSFWorkbook templateWorkbook) {
        this.templateFile = templateFile;
        this.templateWorkbook = templateWorkbook;
        this.writeWorkbooks = new SXSSFWorkbook(templateWorkbook);
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

    public void writeTo(File resultFile) {
        try (OutputStream outputStream = new FileOutputStream(resultFile)) {
            writeWorkbooks.write(outputStream);
            writeWorkbooks.close();
            templateWorkbook.close();
            FileUtils.forceDelete(templateFile);
        } catch (IOException e) {
            throw new SettingException(e);
        }
    }
}
