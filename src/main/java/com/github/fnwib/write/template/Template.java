package com.github.fnwib.write.template;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.databing.LineWriter;
import com.github.fnwib.write.config.ResultFileSetting;
import com.github.fnwib.write.config.TemplateSetting;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.IOException;
import java.util.Optional;

public abstract class Template<T> {
    LineReader<T>       lineReader;
    TemplateSetting     templateSetting;
    ResultFileSetting   resultFileSetting;
    XSSFWorkbook        workbook;

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
            createCellStyle().ifPresent((style) -> lineWriter.setCellStyle(style));
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


    private Optional<CellStyle> createCellStyle() {
        Optional<CellStyle> style = CellStyleBuilder.builder()
                .workbook(workbook)
                .build();
        style.ifPresent((s) -> FontBuilder.builder()
                .workbook(workbook)
                .fontName("Arial")
                .height(((short) 12))
                .build()
                .ifPresent((font -> s.setFont(font))));
        return style;
    }

}

