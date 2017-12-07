package com.github.fnwib.write;

import com.github.fnwib.convert.*;
import com.github.fnwib.parse.ParseImpl;
import com.github.fnwib.parse.Parser;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.read.ExcelReader;
import com.github.fnwib.read.ExcelReaderImpl;
import com.monitorjbl.xlsx.StreamingReader;
import model.WriteModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.*;

public class ExcelWriterImplTest {

    private File tempTemplateFile;

    private File exportFile;

    @Before
    public void initDate() throws IOException {
        ClassLoader classLoader = getClass().getClassLoader();
        String dir = classLoader.getResource("test-file").getFile();
        System.out.println(dir);
        File templateFile = new File(dir + File.separator + "write-template.xlsx");
        tempTemplateFile = new File(dir + File.separator + "write-template-temp.xlsx");
        IOUtils.copy(new FileInputStream(templateFile), new FileOutputStream(tempTemplateFile));
        exportFile = new File(dir + File.separator + "export.xlsx");
    }

    @After
    public void deleteData() {
        tempTemplateFile.delete();
        exportFile.delete();
    }


    @Test
    public void write() throws Exception {
        ExcelGenericConversionService converterRegistry = new ExcelGenericConversionService();
        converterRegistry.addConverter(String.class, new StringExcelConverter());
        converterRegistry.addConverter(LocalDate.class, new LocalDateExcelConverter());
        converterRegistry.addConverter(Map.class, new TitleDescMapExcelConverter());
        converterRegistry.addConverterFactory(Number.class, new NumberExcelConverterFactory());
        Parser<WriteModel> parser = new ParseImpl<>(WriteModel.class, converterRegistry, 0.6);

        ExcelWriter<WriteModel> excelWriter = new ExcelWriterImpl<>(new XSSFWorkbook(tempTemplateFile), exportFile, parser);

        List<WriteModel> source = getDataList(6);
        excelWriter.write(source);
        File file = excelWriter.write2File();
        Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file);
        ExcelReader<WriteModel> excelReader = new ExcelReaderImpl<>(parser, workbook, 0);
        List<WriteModel> target = excelReader.getData();

        Assert.assertSame("集合长度不一致", source.size(), target.size());
        for (int i = 0; i < source.size(); i++) {
            WriteModel sourceModel = source.get(i);
            WriteModel targetModel = target.get(i);
            Assert.assertEquals("字符串不一致", sourceModel.getString(), targetModel.getString());
            Assert.assertEquals("数字不一致", sourceModel.getInteger(), targetModel.getInteger());
            Assert.assertEquals("日期不一致", sourceModel.getLocalDate(), targetModel.getLocalDate());
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
        }

    }

    @Test
    public void writeMergedRegion() throws Exception {

        ExcelGenericConversionService converterRegistry = new ExcelGenericConversionService();
        converterRegistry.addConverter(String.class, new StringExcelConverter());
        converterRegistry.addConverter(LocalDate.class, new LocalDateExcelConverter());
        converterRegistry.addConverter(Map.class, new TitleDescMapExcelConverter());
        converterRegistry.addConverterFactory(Number.class, new NumberExcelConverterFactory());
        Parser<WriteModel> parser = new ParseImpl<>(WriteModel.class, converterRegistry, 0.6);
        ExcelWriter<WriteModel> excelWriter = new ExcelWriterImpl<>(new XSSFWorkbook(tempTemplateFile), exportFile, parser);
        List<WriteModel> source = getDataList(6);
        excelWriter.writeMergedRegion(source, Arrays.asList(0, 1, 2));
        File file = excelWriter.write2File();

        Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file);
        ExcelReader<WriteModel> excelReader = new ExcelReaderImpl<>(parser, workbook, 0);
        List<WriteModel> target = excelReader.getData();
        Assert.assertSame("集合长度不一致", source.size(), target.size());
        for (int i = 0; i < source.size(); i++) {
            WriteModel sourceModel = source.get(i);
            WriteModel targetModel = target.get(i);
            if (i == 0) {
                Assert.assertEquals("字符串不一致", sourceModel.getString(), targetModel.getString());
                Assert.assertEquals("数字不一致", sourceModel.getInteger(), targetModel.getInteger());
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
            }

        }

    }


    private List<WriteModel> getDataList(int length) {
        List<WriteModel> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {
            Map<TitleDesc, String> mapNumber = new HashMap<>();
            mapNumber.put(new TitleDesc("MAP 1", 3), "Map1");
            mapNumber.put(new TitleDesc("MAP 2", 4), "Map2");
            mapNumber.put(new TitleDesc("MAP 3", 5), "Map3");
            Map<TitleDesc, String> mapString = new HashMap<>();
            mapString.put(new TitleDesc("MAP A", 6), "Map1");
            mapString.put(new TitleDesc("MAP B", 7), "Map2");
            mapString.put(new TitleDesc("MAP C", 8), "Map3");
            WriteModel model = WriteModel.builder().string("A").integer(1)
                    .localDate(LocalDate.now()).mapString(mapString).mapNumber(mapNumber).build();
            result.add(model);
        }
        return result;
    }

}