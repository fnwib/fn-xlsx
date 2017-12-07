package com.github.fnwib.write;

import com.github.fnwib.convert.*;
import com.github.fnwib.parse.ParseImpl;
import com.github.fnwib.parse.Parser;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.read.ExcelReader;
import com.github.fnwib.read.ExcelReaderImpl;
import com.monitorjbl.xlsx.StreamingReader;
import model.WriteModel;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
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

        XSSFWorkbook xssfWorkbook = new XSSFWorkbook(tempTemplateFile);
        SXSSFWorkbook sxssfWorkbook = new SXSSFWorkbook(xssfWorkbook);


        ExcelWriter<WriteModel> excelWriter = new ExcelWriterImpl<>(sxssfWorkbook, exportFile, parser);

        List<WriteModel> source = getDataList(6);
        for (int i = 0; i < 6; i++) {
            if (i == 0) {
                WriteModel writeModel = source.get(i);
                writeModel.setString(null);
                writeModel.setIntNum(null);
                writeModel.setLongNum(null);
                writeModel.setLocalDate(null);

                Map<TitleDesc, String> mapNumber = writeModel.getMapNumber();
                mapNumber.remove(new TitleDesc("MAP 1", 3));
            }
        }
        excelWriter.write(source);
        File file = excelWriter.write2File();
        Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file);
        ExcelReader<WriteModel> excelReader = new ExcelReaderImpl<>(parser, workbook, 0);
        List<WriteModel> target = excelReader.getData();

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

        excelWriter.writeMergedRegion(source.subList(0, 1), Arrays.asList(0, 1, 2));
        excelWriter.writeMergedRegion(source.subList(1, source.size()), Arrays.asList(0, 1, 2));
        File file = excelWriter.write2File();

        Workbook workbook = StreamingReader.builder().bufferSize(1024).rowCacheSize(10).open(file);
        ExcelReader<WriteModel> excelReader = new ExcelReaderImpl<>(parser, workbook, 0);
        List<WriteModel> target = excelReader.getData();
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
            }

        }

    }


    private List<WriteModel> getDataList(int length) {
        List<WriteModel> result = new ArrayList<>(length);
        for (int i = 0; i < length; i++) {

            Map<TitleDesc, String> mapNumber = new HashMap<>();
            mapNumber.put(new TitleDesc("MAP 1", 4), "Map1");
            mapNumber.put(new TitleDesc("MAP 2", 5), "Map2");
            mapNumber.put(new TitleDesc("MAP 3", 6), "Map3");
            Map<TitleDesc, String> mapString = new HashMap<>();
            mapString.put(new TitleDesc("MAP A", 7), "Map1");
            mapString.put(new TitleDesc("MAP B", 8), "Map2");
            mapString.put(new TitleDesc("MAP C", 9), "Map3");

            if (i == 0) {
                mapNumber.put(new TitleDesc("MAP 1", 4), null);
                mapString.put(new TitleDesc("MAP C", 9), null);
            }
            WriteModel model = WriteModel.builder().string("A").intNum(1000000000)
                    .longNum(1111111111111111111L)
                    .localDate(LocalDate.now()).mapString(mapString).mapNumber(mapNumber).build();
            result.add(model);
        }
        return result;
    }

}