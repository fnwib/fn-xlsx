package com.github.fnwib.write.config;

import com.github.fnwib.parse.Parser;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Getter
public class WorkbookConfig<T> {

    private final Parser<T>           parser;
    private final ResultFileSetting   resultFileSetting;
    private final TemplateSetting     templateSetting;
    private final ExportType          exportType;

    public WorkbookConfig(Parser<T> parser,
                          ExportType exportType,
                          ResultFileSetting resultFileSetting,
                          TemplateSetting templateSetting) {
        this.parser = parser;
        this.exportType = exportType;
        this.resultFileSetting = resultFileSetting;
        this.templateSetting = templateSetting;
    }


    public List<File> getResultFiles() {
        File resultFolder = resultFileSetting.getResultFolder();
        File[] files = resultFolder.listFiles();
        if (files ==null) return Collections.emptyList();
        return Lists.newArrayList(files);
    }

}
