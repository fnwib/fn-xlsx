package com.github.fnwib.write;

import com.github.fnwib.convert.*;
import com.github.fnwib.parse.ParseImpl;
import com.github.fnwib.parse.Parser;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.read.ExcelReader;
import com.github.fnwib.read.ExcelReaderImpl;
import com.github.fnwib.write.config.ExportType;
import com.github.fnwib.write.config.ResultFileSetting;
import com.github.fnwib.write.config.TemplateSetting;
import com.github.fnwib.write.config.WorkbookConfig;
import com.google.common.collect.Lists;
import com.monitorjbl.xlsx.StreamingReader;
import model.WriteModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ExcelWriterProcessorTest extends ExcelWriterImplBaseTest {

    @Test
    public void write() {
        ExcelGenericConversionService converterRegistry = new ExcelGenericConversionService();
        converterRegistry.addConverter(String.class, new StringExcelConverter());
        converterRegistry.addConverter(LocalDate.class, new LocalDateExcelConverter());
        converterRegistry.addConverter(Map.class, new TitleDescMapExcelConverter());
        converterRegistry.addConverterFactory(Number.class, new NumberExcelConverterFactory());
        Parser<WriteModel> parser = new ParseImpl<>(WriteModel.class, converterRegistry, 0.6);

        ResultFileSetting resultFileSetting = new ResultFileSetting(40, "aaaa2zs2.xlsx", exportFolder);
        TemplateSetting templateSetting = TemplateSetting.builder().template(tempTemplateFile)
                .addLastTitles(Lists.newArrayList("AAA"))
                .build();
        WorkbookConfig writeConfig = new WorkbookConfig(parser, ExportType.SingleSheet, resultFileSetting, templateSetting);
        ExcelWriterProcessor<WriteModel> writerProcessor = new ExcelWriterProcessor<>(writeConfig);

        List<WriteModel> source = getDataList(6);
        writerProcessor.write(source);
        writerProcessor.write2File();
        List<WriteModel> target = new ArrayList<>();
        for (File file2 : writerProcessor.getFiles()) {
            Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file2);
            ExcelReader<WriteModel> excelReader = new ExcelReaderImpl<>(parser, workbook, 0);
            target.addAll(excelReader.getData());
        }
        Assert.assertSame("集合长度不一致", source.size(), target.size());
        for (int i = 0; i < source.size(); i++) {
            WriteModel sourceModel = source.get(i);
            WriteModel targetModel = target.get(i);
            Assert.assertEquals("字符串不一致", StringUtils.trimToEmpty(sourceModel.getString()), StringUtils.trimToEmpty(targetModel.getString()));
            Assert.assertEquals("数字int不一致", sourceModel.getIntNum(), targetModel.getIntNum());
            Assert.assertEquals("数字long不一致", sourceModel.getLongNum(), targetModel.getLongNum());
            Assert.assertEquals("日期不一致", sourceModel.getLocalDate(), targetModel.getLocalDate());
            Map<TitleDesc, String> sourceNumberMap = sourceModel.getMapNumber();
            Map<TitleDesc, String> targetNumberMap = targetModel.getMapNumber();
            System.out.println(sourceNumberMap);
            System.out.println(targetNumberMap);
            sourceNumberMap.forEach((titleDesc, s) -> {
                String s1 = targetNumberMap.get(titleDesc);
                Assert.assertEquals("MAP number 值不一致", StringUtils.trimToEmpty(s), StringUtils.trimToEmpty(s1));
            });

            Map<TitleDesc, String> sourceStringMap = sourceModel.getMapString();
            Map<TitleDesc, String> targetStringMap = targetModel.getMapString();
            sourceStringMap.forEach((titleDesc, s) -> {
                String s1 = targetStringMap.get(titleDesc);
                Assert.assertEquals("MAP String 值不一致", StringUtils.trimToEmpty(s), StringUtils.trimToEmpty(s1));
            });
            Assert.assertEquals("动态添加的列AAA", sourceModel.getAaa(), targetModel.getAaa());
        }

    }


    @Test
    public void writeMergedRegion() {

        ExcelGenericConversionService converterRegistry = new ExcelGenericConversionService();
        converterRegistry.addConverter(String.class, new StringExcelConverter());
        converterRegistry.addConverter(LocalDate.class, new LocalDateExcelConverter());
        converterRegistry.addConverter(Map.class, new TitleDescMapExcelConverter());
        converterRegistry.addConverterFactory(Number.class, new NumberExcelConverterFactory());
        Parser<WriteModel> parser = new ParseImpl<>(WriteModel.class, converterRegistry, 0.6);

        ResultFileSetting resultFileSetting = new ResultFileSetting(2, "aaaa2zs2.xlsx", exportFolder);
        TemplateSetting templateSetting = TemplateSetting.builder().template(tempTemplateFile)
                .addLastTitles(Lists.newArrayList("AAA"))
                .build();
        WorkbookConfig writeConfig = new WorkbookConfig(parser, ExportType.SingleSheet, resultFileSetting, templateSetting);
        ExcelWriterProcessor<WriteModel> writerProcessor = new ExcelWriterProcessor<>(writeConfig);

        List<WriteModel> source = getDataList(6);

        writerProcessor.writeMergedRegion(source.subList(0, 1), Arrays.asList(0, 1, 2));
        writerProcessor.writeMergedRegion(source.subList(1, source.size()), Arrays.asList(0, 1, 2));
        writerProcessor.write2File();
        List<WriteModel> target = new ArrayList<>();
        List<File> files = writerProcessor.getFiles();
        for (File file1 : files) {
            Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file1);
            ExcelReader<WriteModel> excelReader = new ExcelReaderImpl<>(parser, workbook, 0);
            target.addAll(excelReader.getData());
        }
        Assert.assertSame("集合长度不一致", source.size(), target.size());
        for (int i = 0; i < source.size(); i++) {
            WriteModel sourceModel = source.get(i);
            WriteModel targetModel = target.get(i);
            if (i == 0) {
                Assert.assertEquals("字符串不一致", StringUtils.trimToEmpty(sourceModel.getString()), StringUtils.trimToEmpty(targetModel.getString()));
                Assert.assertEquals("数字int不一致", sourceModel.getIntNum(), targetModel.getIntNum());
                Assert.assertEquals("数字long不一致", sourceModel.getLongNum(), targetModel.getLongNum());
                Assert.assertEquals("日期不一致", sourceModel.getLocalDate(), targetModel.getLocalDate());
            } else {
                Map<TitleDesc, String> sourceNumberMap = sourceModel.getMapNumber();
                Map<TitleDesc, String> targetNumberMap = targetModel.getMapNumber();
                sourceNumberMap.forEach((titleDesc, s) -> {
                    String s1 = targetNumberMap.get(titleDesc);
                    Assert.assertEquals("MAP number 值不一致", s, s1);
                });
                Map<TitleDesc, String> sourceStringMap = sourceModel.getMapString();
                Map<TitleDesc, String> targetStringMap = targetModel.getMapString();
                sourceStringMap.forEach((titleDesc, s) -> {
                    String s1 = targetStringMap.get(titleDesc);
                    Assert.assertEquals("MAP String 值不一致", s, s1);
                });
                Assert.assertEquals("动态添加的列AAA", sourceModel.getAaa(), targetModel.getAaa());
            }

        }

    }

    public static void main(String[] args) {
        String separator = File.separator;
        System.out.println(separator);
        String pathSeparator = File.pathSeparator;
        System.out.println(pathSeparator);
    }
}