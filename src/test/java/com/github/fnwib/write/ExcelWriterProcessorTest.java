package com.github.fnwib.write;

import com.github.fnwib.databing.LineReader;
import com.github.fnwib.databing.LineReaderForExcel;
import com.github.fnwib.read.ExcelReader;
import com.github.fnwib.read.ExcelReaderImpl;
import com.github.fnwib.write.config.ResultFileSetting;
import com.github.fnwib.write.config.TemplateSetting;
import com.github.fnwib.write.config.WorkbookBuilder;
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
    /**
     * 动态模版
     */
    @Test
    public void dynamicTemplateWrite() {
        ResultFileSetting resultFileSetting = new ResultFileSetting(4, "aaaa2zs2", exportFolder);
        TemplateSetting templateSetting = new TemplateSetting();
        templateSetting.addLastTitles(Lists.newArrayList("AAA", "序号"));
        templateSetting.addCellText(new CellText(0, 0, "标题"));
        templateSetting.useDefaultCellStyle();
        LineReader<WriteModel> lineReader = new LineReaderForExcel<>(WriteModel.class);
        WorkbookConfig writeConfig = new WorkbookBuilder<>(lineReader,resultFileSetting, templateSetting);
        List<WriteModel> source = getDataList(6);

        List<WriteModel> target = writeAndRead(writeConfig);

        Assert.assertSame("集合长度不一致", source.size(), target.size());
        for (int i = 0; i < source.size(); i++) {
            WriteModel sourceModel = source.get(i);
            WriteModel targetModel = target.get(i);
            Assert.assertEquals("动态添加的列 AAA", sourceModel.getAaa(), targetModel.getAaa());
            Assert.assertEquals("动态添加的列 序号", sourceModel.getSequence(), targetModel.getSequence());
        }

    }

    private List<WriteModel> writeAndRead(WorkbookConfig writeConfig) {
        List<WriteModel> source = getDataList(6);
        List<WriteModel> target = new ArrayList<>();
        ExcelWriter<WriteModel> writerProcessor = new ExcelWriterProcessor<>(writeConfig);
        writerProcessor.write(source);
        List<File> files = writerProcessor.getFiles();
        LineReader<WriteModel> lineReader = new LineReaderForExcel<>(WriteModel.class);
        for (File file2 : files) {
            Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file2);
            ExcelReader<WriteModel> excelReader = new ExcelReaderImpl<>(lineReader, workbook, 0);
            List<WriteModel> data = excelReader.fetchAllData();
            System.out.println(excelReader.hasNext());
            target.addAll(data);
            String preTitle = excelReader.getPreTitle(0, 0);
            Assert.assertEquals("0,0 标题不一致", "标题", preTitle);
        }
        Collections.sort(target, Comparator.comparing(WriteModel::getSequence));
        return target;
    }

    @Test
    public void write() {
        ResultFileSetting resultFileSetting = new ResultFileSetting(4, "aaaa2zs2", exportFolder);

        TemplateSetting templateSetting = new TemplateSetting();
        templateSetting.setTemplate(tempTemplateFile);
        templateSetting.addLastTitles(Lists.newArrayList("AAA", "序号"));
        templateSetting.addCellText(new CellText(0, 0, "标题"));
        templateSetting.useDefaultCellStyle();

        LineReader<WriteModel> lineReader = new LineReaderForExcel<>(WriteModel.class);
        WorkbookConfig writeConfig = new WorkbookBuilder<>(lineReader, resultFileSetting, templateSetting);

        List<WriteModel> target = writeAndRead(writeConfig);
        List<WriteModel> source = getDataList(6);

        Assert.assertSame("集合长度不一致", source.size(), target.size());
        for (int i = 0; i < source.size(); i++) {
            WriteModel sourceModel = source.get(i);
            WriteModel targetModel = target.get(i);
            Assert.assertEquals("字符串不一致", StringUtils.trimToEmpty(sourceModel.getString()), StringUtils.trimToEmpty(targetModel.getString()));
            Assert.assertEquals("数字int不一致", sourceModel.getIntNum(), targetModel.getIntNum());
            Assert.assertEquals("数字long不一致", sourceModel.getLongNum(), targetModel.getLongNum());
            Assert.assertEquals("日期不一致", sourceModel.getLocalDate(), targetModel.getLocalDate());

            List<String> sourceNumberList = sourceModel.getListNumber();
            List<String> targetNumberList = targetModel.getListNumber();
            Assert.assertArrayEquals("List number 值不一致", sourceNumberList.toArray(), targetNumberList.toArray());

            List<String> sourceStringMap = sourceModel.getListNumber();
            List<String> targetStringMap = targetModel.getListNumber();
            Assert.assertArrayEquals("List String 值不一致", sourceStringMap.toArray(), targetStringMap.toArray());

            Assert.assertEquals("动态添加的列AAA", sourceModel.getAaa(), targetModel.getAaa());
            Assert.assertEquals("enumType", sourceModel.getEnumType(), targetModel.getEnumType());
        }

    }


    @Test
    public void writeMergedRegion() {
        ResultFileSetting resultFileSetting = new ResultFileSetting(2, "aaaa2zs2.xlsx", exportFolder);

        TemplateSetting templateSetting = new TemplateSetting();
        templateSetting.setTemplate(tempTemplateFile);
        templateSetting.addLastTitles(Lists.newArrayList("AAA", "序号"));

        final LineReader<WriteModel> lineReader = new LineReaderForExcel<>(WriteModel.class);
        WorkbookConfig writeConfig = new WorkbookBuilder<>(lineReader, resultFileSetting, templateSetting);
        ExcelWriterProcessor<WriteModel> writerProcessor = new ExcelWriterProcessor<>(writeConfig);

        List<WriteModel> source = getDataList(6);

        writerProcessor.writeMergedRegion(source.subList(0, 1), Arrays.asList(0, 1, 2));
        writerProcessor.writeMergedRegion(source.subList(1, 3), Arrays.asList(0, 1, 2));
        writerProcessor.writeMergedRegion(source.subList(3, source.size()), Arrays.asList(0, 1, 2));
        List<WriteModel> target = new ArrayList<>();
        List<File> files = writerProcessor.getFiles();
        for (File file1 : files) {
            Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file1);
            ExcelReader<WriteModel> excelReader = new ExcelReaderImpl<>(lineReader, workbook, 0);
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
                List<String> sourceNumberList = sourceModel.getListNumber();
                List<String> targetNumberList = targetModel.getListNumber();
                Assert.assertArrayEquals("List number 值不一致", sourceNumberList.toArray(), targetNumberList.toArray());

                List<String> sourceStringMap = sourceModel.getListNumber();
                List<String> targetStringMap = targetModel.getListNumber();
                Assert.assertArrayEquals("List String 值不一致", sourceStringMap.toArray(), targetStringMap.toArray());
                Assert.assertEquals("动态添加的列AAA", sourceModel.getAaa(), targetModel.getAaa());
            }

        }

    }


}