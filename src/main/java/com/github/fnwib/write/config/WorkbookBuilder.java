package com.github.fnwib.write.config;

import com.github.fnwib.databing.LineWriter;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.write.template.Template;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class WorkbookBuilder<T> {

    private static final Logger log = LoggerFactory.getLogger(WorkbookBuilder.class);

    private static final int defaultSheetIndex = 0;
    private final Template template;

    private SXSSFWorkbook     writeWorkbooks;
    private ResultFileSetting resultFileSetting;

    private boolean written;

    public WorkbookBuilder(WorkbookConfig workbookConfig) {
        this.template = workbookConfig.getTemplate();
        this.resultFileSetting = template.getResultFileSetting();
        this.written = false;
    }

    public boolean isWritten() {
        return written;
    }

    public int getTitleRowNum() {
        return template.getTiltRowNum();
    }

    public LineWriter<T> getWriteParser() {
        if (written) {
            throw new ExcelException("excel已经写入文件");
        }
        try {
            writeWorkbooks = this.template.getWriteWorkbook();
            Sheet sheet = getNextSheet();
            LineWriter lineWriter = this.template.getLineWriter();
            lineWriter.setSheet(sheet);
            return lineWriter;
        } catch (IOException e) {
            throw new ExcelException(e);
        }
    }

    private Sheet getNextSheet() {
        written = true;
        return writeWorkbooks.getSheetAt(defaultSheetIndex);
    }

    public void write() {
        try (OutputStream outputStream = new FileOutputStream(resultFileSetting.getNextResultFile())) {
            writeWorkbooks.write(outputStream);
            writeWorkbooks.close();
            written = false;
        } catch (IOException e) {
            throw new ExcelException(e);
        }
    }

    public boolean canWrite(int rowNum) {
        return resultFileSetting.gt(rowNum);
    }

    public List<File> getResultFiles() {
        File resultFolder = resultFileSetting.getResultFolder();
        File[] files = resultFolder.listFiles();
        if (files == null) return Collections.emptyList();
        return Lists.newArrayList(files);
    }

}
