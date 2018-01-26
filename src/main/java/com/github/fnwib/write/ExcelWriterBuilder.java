package com.github.fnwib.write;

import com.github.fnwib.convert.*;
import com.github.fnwib.parse.ParseImpl;
import com.github.fnwib.parse.Parser;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.write.config.ExportType;
import com.github.fnwib.write.config.ResultFileSetting;
import com.github.fnwib.write.config.TemplateSetting;
import com.github.fnwib.write.config.WorkbookConfig;
import org.apache.poi.ss.usermodel.CellStyle;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExcelWriterBuilder {

    public static ExcelWriterBuilder.Builder builder() {

        return new ExcelWriterBuilder.Builder();
    }

    public static class Builder<T> {

        private Class<T> entityClass;

        private ExportType exportType;

        private String filename;

        private File resultFolder;

        private File template;

        private String sheetName;

        private List<String> addLastTitles = new ArrayList<>();

        public Builder entityClass(Class<T> entityClass) {
            this.entityClass = entityClass;
            return this;
        }

        public Builder exportType(ExportType exportType) {
            this.exportType = exportType;
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

        public ExcelWriter<T> build() {

            ResultFileSetting resultFileSetting = new ResultFileSetting(filename, resultFolder);
            TemplateSetting templateSetting = TemplateSetting.builder().template(template)
                    .useDefaultCellStyle(true).addLastTitles(addLastTitles).sheetName(sheetName).build();
            ExcelGenericConversionService converterRegistry = new ExcelGenericConversionService();
            converterRegistry.addConverter(new StringExcelConverter());
            converterRegistry.addConverter(new LocalDateExcelConverter());
            converterRegistry.addConverter(new TitleDescMapExcelConverter());
            converterRegistry.addConverterFactory(new NumberExcelConverterFactory());
            Parser<T> parser = new ParseImpl<>(entityClass, converterRegistry, 0.6);

            WorkbookConfig<T> workbookConfig = new WorkbookConfig<>(parser,ExportType.SingleSheet, resultFileSetting, templateSetting);
            return new ExcelWriterProcessor<>(workbookConfig);
        }

    }
}
