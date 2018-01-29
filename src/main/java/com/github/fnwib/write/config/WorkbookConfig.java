package com.github.fnwib.write.config;

import com.github.fnwib.databing.ExcelLineReader;
import com.github.fnwib.databing.LineReader;
import com.github.fnwib.parse.Parser;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class WorkbookConfig<T> {

    private final Class<T>          entityClass;
    private final LineReader<T>     lineReader;
    private final ResultFileSetting resultFileSetting;
    private final TemplateSetting   templateSetting;

    @Deprecated
    public WorkbookConfig(Parser<T> parser,
                          ExportType exportType,
                          ResultFileSetting resultFileSetting,
                          TemplateSetting templateSetting) {
        this.entityClass = parser.getClazz();
        this.lineReader = new ExcelLineReader<>(entityClass);
        this.resultFileSetting = resultFileSetting;
        this.templateSetting = templateSetting;
    }

    public WorkbookConfig(Class<T> entityClass,
                          ResultFileSetting resultFileSetting,
                          TemplateSetting templateSetting) {
        this.entityClass = entityClass;
        this.lineReader = new ExcelLineReader<>(entityClass);
        this.resultFileSetting = resultFileSetting;
        this.templateSetting = templateSetting;
    }


    public List<File> getResultFiles() {
        File resultFolder = resultFileSetting.getResultFolder();
        File[] files = resultFolder.listFiles();
        if (files == null) return Collections.emptyList();
        return Lists.newArrayList(files);
    }


    public LineReader<T> getLineReader() {
        return lineReader;
    }

    public ResultFileSetting getResultFileSetting() {
        return resultFileSetting;
    }

    public TemplateSetting getTemplateSetting() {
        return templateSetting;
    }
}
