package com.github.fnwib.write.template;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.exception.ExcelException;
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
import java.util.Optional;

public class EmptyTemplate<T> extends Template<T> {

    private static final Logger log = LoggerFactory.getLogger(EmptyTemplate.class);

    private File file;
    private int  titleRowNum;

    public EmptyTemplate(LineReader<T> lineReader,
                         TemplateSetting templateSetting,
                         ResultFileSetting resultFileSetting) {
        super(lineReader, templateSetting, resultFileSetting);
        file = buildWorkbook();
    }

    @Override
    public SXSSFWorkbook getWriteWorkbook() throws IOException {
        FileInputStream inputStream = FileUtils.openInputStream(file);
        this.workbook = new XSSFWorkbook(inputStream);
        return new SXSSFWorkbook(workbook);
    }

    @Override
    public int getTiltRowNum() {
        return titleRowNum;
    }

    private File buildWorkbook() {
        try {
            Workbook workbook = new XSSFWorkbook();
            workbook.createSheet();
            buildSheet(workbook, 0);
            File emptyFile = resultFileSetting.getEmptyFile();
            workbook.write(FileUtils.openOutputStream(emptyFile));
            return emptyFile;
        } catch (IOException e) {
            log.error("open workbook error ", e);
            throw new ExcelException("模版错误");
        }
    }

    void buildSheet(Workbook workbook, int sheetIndex) {
        Sheet sheet = workbook.getSheetAt(sheetIndex);
        if (StringUtils.isNotBlank(templateSetting.getSheetName())) {
            workbook.setSheetName(sheetIndex, templateSetting.getSheetName());
        }
        if (templateSetting.changed()) {
            List<CellText> cellTexts = templateSetting.getCellTexts();

            int nextRowNum;
            if (!cellTexts.isEmpty()) {
                for (CellText cellText : cellTexts) {
                    Row row = getRow(sheet, cellText.getRowNum());
                    Cell cell = getCell(row, cellText.getCellNum());
                    cell.setCellValue(cellText.getText());
                }
                nextRowNum = cellTexts.stream().mapToInt(CellText::getRowNum).max().getAsInt() + 1;
            } else {
                nextRowNum = 0;
            }
            titleRowNum = nextRowNum;
            Row row = getRow(sheet, nextRowNum);
            int cellNum = row.getLastCellNum();
            Optional<CellStyle> cellStyle = createTitleCellStype(workbook);
            for (String title : templateSetting.getAddLastTitles()) {
                cellNum++;
                Cell cell = row.createCell(cellNum);
                cellStyle.ifPresent((style) -> cell.setCellStyle(style));
                cell.setCellValue(title);
            }
            lineReader.match(row);
        }
    }


    private Optional<CellStyle> createTitleCellStype(Workbook workbook) {
        Optional<CellStyle> style = CellStyleBuilder.builder()
                .workbook(workbook)
                .fillBackgroundColor(IndexedColors.GREEN)
                .fillForegroundColor(IndexedColors.BRIGHT_GREEN)
                .fillPattern(FillPatternType.SOLID_FOREGROUND)
                .build();
        style.ifPresent((s) -> FontBuilder.builder()
                .workbook(workbook)
                .fontName("黑体")
                .height(((short) 14))
                .build()
                .ifPresent((font -> s.setFont(font))));
        return style;
    }

}