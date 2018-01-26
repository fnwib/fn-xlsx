package com.github.fnwib.write;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.parse.ParseImpl;
import com.github.fnwib.parse.Parser;
import com.github.fnwib.read.ExcelReader;
import com.github.fnwib.read.ExcelReaderImpl;
import com.github.fnwib.read.ExcelReaderImpl2;
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
import java.util.*;

public class ExcelWriterProcessorTest extends ExcelWriterImplBaseTest {

    @Test
    public void write() {
        ResultFileSetting resultFileSetting = new ResultFileSetting(4, "aaaa2zs2", exportFolder);
        TemplateSetting templateSetting = TemplateSetting.builder().template(tempTemplateFile)
                .addLastTitles(Lists.newArrayList("AAA", "序号"))
                .cellText(new CellText(0, 0, "标题"))
                .useDefaultCellStyle(true)
                .build();
        List<WriteModel> source = getDataList(6);
        List<WriteModel> target = new ArrayList<>();
        WorkbookConfig<WriteModel> writeConfig = new WorkbookConfig(WriteModel.class, resultFileSetting, templateSetting);
        ExcelWriter<WriteModel> writerProcessor = new ExcelWriterProcessor<>(writeConfig);
        writerProcessor.write(source);
        for (File file2 : writerProcessor.getFiles()) {
            Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file2);
            LineReader<WriteModel> lineReader = writeConfig.getLineReader();
            ExcelReader<WriteModel> excelReader = new ExcelReaderImpl2<>(lineReader, workbook, 0);
            target.addAll(excelReader.getData());
            String preTitle = excelReader.getPreTitle(0, 0);
            Assert.assertEquals("0,0 标题不一致", "标题", preTitle);
        }
        Collections.sort(target, Comparator.comparing(WriteModel::getSequence));
        Assert.assertSame("集合长度不一致", source.size(), target.size());
        for (int i = 0; i < source.size(); i++) {
            WriteModel sourceModel = source.get(i);
            WriteModel targetModel = target.get(i);
            Assert.assertEquals("字符串不一致", StringUtils.trimToEmpty(sourceModel.getString()), StringUtils.trimToEmpty(targetModel.getString()));
            Assert.assertEquals("数字int不一致", sourceModel.getIntNum(), targetModel.getIntNum());
            Assert.assertEquals("数字long不一致", sourceModel.getLongNum(), targetModel.getLongNum());
            Assert.assertEquals("日期不一致", sourceModel.getLocalDate(), targetModel.getLocalDate());
            Map<Integer, String> sourceNumberMap = sourceModel.getMapNumber();
            Map<Integer, String> targetNumberMap = targetModel.getMapNumber();
            System.out.println(sourceNumberMap);
            System.out.println(targetNumberMap);
            sourceNumberMap.forEach((titleDesc, s) -> {
                String s1 = targetNumberMap.get(titleDesc);
                Assert.assertEquals("MAP number 值不一致", StringUtils.trimToEmpty(s), StringUtils.trimToEmpty(s1));
            });

            Map<Integer, String> sourceStringMap = sourceModel.getMapString();
            Map<Integer, String> targetStringMap = targetModel.getMapString();
            sourceStringMap.forEach((titleDesc, s) -> {
                String s1 = targetStringMap.get(titleDesc);
                Assert.assertEquals("MAP String 值不一致", StringUtils.trimToEmpty(s), StringUtils.trimToEmpty(s1));
            });
            Assert.assertEquals("动态添加的列AAA", sourceModel.getAaa(), targetModel.getAaa());
            Assert.assertEquals("enumType", sourceModel.getEnumType(), targetModel.getEnumType());
        }

    }


    @Test
    public void writeMergedRegion() {
        ResultFileSetting resultFileSetting = new ResultFileSetting(2, "aaaa2zs2.xlsx", exportFolder);
        TemplateSetting templateSetting = TemplateSetting.builder().template(tempTemplateFile)
                .addLastTitles(Lists.newArrayList("AAA", "序号"))
                .build();
        WorkbookConfig writeConfig = new WorkbookConfig(WriteModel.class, resultFileSetting, templateSetting);
        ExcelWriterProcessor<WriteModel> writerProcessor = new ExcelWriterProcessor<>(writeConfig);

        List<WriteModel> source = getDataList(6);

        writerProcessor.writeMergedRegion(source.subList(0, 1), Arrays.asList(0, 1, 2));
        writerProcessor.writeMergedRegion(source.subList(1, 3), Arrays.asList(0, 1, 2));
        writerProcessor.writeMergedRegion(source.subList(3, source.size()), Arrays.asList(0, 1, 2));
        List<WriteModel> target = new ArrayList<>();
        List<File> files = writerProcessor.getFiles();
        for (File file1 : files) {
            Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file1);
            LineReader<WriteModel> lineReader = writeConfig.getLineReader();
            ExcelReader<WriteModel> excelReader = new ExcelReaderImpl2<>(lineReader, workbook, 0);
            target.addAll(excelReader.getData());
        }
        Collections.sort(target, Comparator.comparing(WriteModel::getSequence));
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
                Map<Integer, String> sourceNumberMap = sourceModel.getMapNumber();
                Map<Integer, String> targetNumberMap = targetModel.getMapNumber();
                sourceNumberMap.forEach((titleDesc, s) -> {
                    String s1 = targetNumberMap.get(titleDesc);
                    Assert.assertEquals("MAP number 值不一致", s, s1);
                });
                Map<Integer, String> sourceStringMap = sourceModel.getMapString();
                Map<Integer, String> targetStringMap = targetModel.getMapString();
                sourceStringMap.forEach((titleDesc, s) -> {
                    String s1 = targetStringMap.get(titleDesc);
                    Assert.assertEquals("MAP String 值不一致", s, s1);
                });
                Assert.assertEquals("动态添加的列AAA", sourceModel.getAaa(), targetModel.getAaa());
            }

        }

    }


}