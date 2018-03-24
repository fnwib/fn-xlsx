package com.github.fnwib.write.template;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.databing.LineWriter;
import com.github.fnwib.write.config.ResultFileSetting;
import com.github.fnwib.write.config.TemplateSetting;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;

public abstract class Template<T> {
    LineReader<T>     lineReader;
    TemplateSetting   templateSetting;
    ResultFileSetting resultFileSetting;
    XSSFWorkbook      workbook;

    public Template(LineReader<T> lineReader,
                    TemplateSetting templateSetting,
                    ResultFileSetting resultFileSetting) {
        this.lineReader = lineReader;
        this.templateSetting = templateSetting;
        this.resultFileSetting = resultFileSetting;
    }

    public abstract int getTiltRowNum();

    public abstract SXSSFWorkbook getWriteWorkbook() throws IOException;

    public LineWriter getLineWriter() {
        LineWriter<T> lineWriter = lineReader.getLineWriter();
        if (templateSetting.isUseDefaultCellStyle()) {
            CellStyle cellStyle = getCellStyle();
            lineWriter.setCellStyle(cellStyle);
        }
        return lineWriter;
    }

    public ResultFileSetting getResultFileSetting() {
        return resultFileSetting;
    }


    Row getRow(Sheet sheet, int rowNum) {
        Row row = sheet.getRow(rowNum);
        return row == null ? sheet.createRow(rowNum) : row;
    }

    Cell getCell(Row row, int cellNum) {
        Cell cell = row.getCell(cellNum);
        return cell == null ? row.createCell(cellNum) : cell;
    }

    CellStyle getCellStyle() {
        CellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setAlignment(HorizontalAlignment.CENTER); // 居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        cellStyle.setDataFormat((short) BuiltinFormats.getBuiltinFormat("text"));
        Font font2 = workbook.createFont();
        font2.setFontName("Arial");
        font2.setFontHeightInPoints((short) 10);
        cellStyle.setFont(font2);
        return cellStyle;
    }
}

