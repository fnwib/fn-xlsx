package com.github.fnwib.write;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.databing.LineReaderForExcel;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.write.config.ResultFileSetting;
import com.github.fnwib.write.config.TemplateSetting;
import com.github.fnwib.write.config.WorkbookConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ExcelWriterBuilder {

    public static ExcelWriterBuilder.Builder builder() {

        return new ExcelWriterBuilder.Builder();
    }

    public static class Builder<T> {

        private Class<T> entityClass;

        private String filename;

        private File resultFolder;

        private File template;

        private String sheetName;

        private List<String> addLastTitles = new ArrayList<>();

        private LocalConfig localConfig;

        public Builder entityClass(Class<T> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public Builder filename(String filename) {
            this.filename = filename;
            return this;
        }

        public Builder resultFolder(File resultFolder) {
            this.resultFolder = resultFolder;
            return this;
        }

        public Builder template(File template) {
            this.template = template;
            return this;
        }

        public Builder sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        public Builder addLastTitles(List<String> titles) {
            this.addLastTitles.addAll(titles);
            return this;
        }

        public Builder addLastTitle(String title) {
            this.addLastTitles.add(title);
            return this;
        }


        public Builder localConfig(LocalConfig localConfig) {
            this.localConfig = localConfig;
            return this;
        }

        public ExcelWriter<T> build() {

            ResultFileSetting resultFileSetting = new ResultFileSetting(filename, resultFolder);
            TemplateSetting templateSetting = new TemplateSetting();
            templateSetting.setTemplate(template);
            templateSetting.useDefaultCellStyle();
            templateSetting.addLastTitles(addLastTitles);
            templateSetting.setSheetName(sheetName);
            LineReader<T> lineReader;
            if (localConfig == null) {
                lineReader = new LineReaderForExcel<>(entityClass);
            } else {
                lineReader = new LineReaderForExcel<>(entityClass, localConfig);
            }
            WorkbookConfig<T> workbookConfig = new WorkbookConfig<>(lineReader, resultFileSetting, templateSetting);
            return new ExcelWriterProcessor<>(workbookConfig);
        }

    }
}
