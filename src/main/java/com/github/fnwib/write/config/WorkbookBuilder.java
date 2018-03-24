package com.github.fnwib.write.config;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.databing.LineWriter;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.write.template.EmptyTemplate;
import com.github.fnwib.write.template.ExistTemplate;
import com.github.fnwib.write.template.Template;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;

public class WorkbookBuilder<T> implements WorkbookConfig {

    private static final int defaultSheetIndex = 0;

    private final ResultFileSetting resultFileSetting;

    private final Template template;

    private SXSSFWorkbook writeWorkbooks;
    private boolean       written;

    public WorkbookBuilder(LineReader<T> lineReader,
                           ResultFileSetting resultFileSetting,
                           TemplateSetting templateSetting) {
        this.resultFileSetting = resultFileSetting;
        this.template = getTemplate(lineReader, templateSetting, resultFileSetting.getEmptyFile());
        this.written = false;
    }

    private Template<T> getTemplate(LineReader<T> lineReader, TemplateSetting templateSetting, File file) {
        File template = templateSetting.getTemplate();

        if (template == null) {
            if (templateSetting.changed()) {

                return new EmptyTemplate<>(lineReader, templateSetting, file);
            } else {
                throw new SettingException("模版没有配置");
            }
        }
        if (!template.exists()) {
            throw new SettingException("模版" + template.getAbsolutePath() + "不存在");
        }
        return new ExistTemplate<>(lineReader, templateSetting, file);
    }

    @Override
    public boolean isWritten() {
        return written;
    }

    @Override
    public int getTitleRowNum() {
        return template.getTiltRowNum();
    }

    @Override
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

    @Override
    public Sheet getNextSheet() {
        written = true;
        return writeWorkbooks.getSheetAt(defaultSheetIndex);
    }

    @Override
    public void write() {
        try (OutputStream outputStream = new FileOutputStream(resultFileSetting.getNextResultFile())) {
            writeWorkbooks.write(outputStream);
            writeWorkbooks.close();
            written = false;
        } catch (IOException e) {
            throw new ExcelException(e);
        }
    }

    @Override
    public boolean canWrite(int rowNum) {
        return template.gt(rowNum);
    }

    @Override
    public List<File> getResultFiles() {
        File resultFolder = resultFileSetting.getResultFolder();
        File[] files = resultFolder.listFiles();
        if (files == null) return Collections.emptyList();
        return Lists.newArrayList(files);
    }
}
