package com.github.fnwib.write.config;

import com.github.fnwib.databing.LineReader;
import com.google.common.collect.Lists;

import java.io.File;
import java.util.Collections;
import java.util.List;

public class WorkbookConfig<T> {

    private final LineReader<T>     lineReader;
    private final ResultFileSetting resultFileSetting;
    private final TemplateSetting   templateSetting;

    public WorkbookConfig(LineReader<T> lineReader,
                          ResultFileSetting resultFileSetting,
                          TemplateSetting templateSetting) {
        this.lineReader = lineReader;
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
