package com.github.fnwib.write;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.parse.Parser;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Excel生成工具
 * <p>
 * 注意：合并单元格操作会修改模版，建议模版使用副本
 *
 * @param <T>
 */
public class ExcelWriterImpl<T> implements ExcelWriter<T>, AutoCloseable {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExcelWriter.class);

    private CellStyle cellStyle;

    private final File exportFile;

    private final Parser<T> parser;

    private final AtomicReference<Sheet> currentSheet = new AtomicReference<>();

    private final AtomicInteger currentRowNum = new AtomicInteger();

    private final Workbook workbook;

    private WriteParser<T> writeParser;

    public ExcelWriterImpl(Workbook workbook,
                           CellStyle cellStyle,
                           File exportFile,
                           Parser<T> parser) {
        this.exportFile = exportFile;
        this.parser = parser;
        this.workbook = workbook;
        this.cellStyle = cellStyle;
        currentSheet.set(workbook.getSheetAt(0));
        initTitleRow();
    }


    private void initTitleRow() {
        boolean flag = false;
        Sheet sheet;
        if (SXSSFWorkbook.class == workbook.getClass()) {
            SXSSFWorkbook sxssfWorkbook = (SXSSFWorkbook) workbook;
            XSSFWorkbook xssfWorkbook = sxssfWorkbook.getXSSFWorkbook();
            sheet = xssfWorkbook.getSheetAt(0);
        } else {
            sheet = currentSheet.get();
        }
        for (Row row : sheet) {
            boolean matched = parser.match(row);
            if (matched) {
                currentRowNum.set(row.getRowNum() + 1);
                writeParser = parser.createWriteParser();
                writeParser.defaultCellStyle(cellStyle);
                flag = true;
                break;
            }
        }
        if (!flag) {
            throw new ExcelException("模版错误");
        }
    }

    @Override
    public void write(T element) {
        writeParser.convert(currentSheet.get(), currentRowNum.getAndAdd(1), element);
    }

    @Override
    public void write(List<T> elements) {
        for (T element : elements) {
            writeParser.convert(currentSheet.get(), currentRowNum.getAndAdd(1), element);
        }
    }

    @Override
    public void writeMergedRegion(List<T> elements, List<Integer> mergedRangeIndexes) {
        if (elements.isEmpty()) {
            return;
        }
        if (elements.size() == 1) {
            writeParser.convert(currentSheet.get(), currentRowNum.getAndAdd(1), elements.get(0));
        } else {
            writeParser.convert(currentSheet.get(), currentRowNum.getAndAdd(elements.size()), elements, mergedRangeIndexes);
        }
    }

    @Override
    public File write2File() throws IOException {
        try (OutputStream outputStream = new FileOutputStream(exportFile)) {
            workbook.write(outputStream);
            workbook.close();
        }
        return exportFile;
    }

    @Override
    public void close() throws Exception {
        workbook.close();
    }
}

