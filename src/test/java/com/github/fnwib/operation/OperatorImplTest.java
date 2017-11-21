package com.github.fnwib.operation;

import com.github.fnwib.convert.*;
import com.monitorjbl.xlsx.StreamingReader;
import model.Model;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class OperatorImplTest {

    private File file;

    @Before
    public void initDate() {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource("test.xlsx").getFile());
    }

    @Test
    public void convert() {
        ExcelGenericConversionService converterRegistry = new ExcelGenericConversionService();
        converterRegistry.addConverter(String.class, new StringExcelConverter());
        converterRegistry.addConverter(LocalDate.class, new LocalDateExcelConverter());
        converterRegistry.addConverter(Map.class, new SeqKeyMapExcelConverter());
        converterRegistry.addConverterFactory(Number.class, new NumberExcelConverterFactory());
        Operator<Model> operator = new OperatorImpl<>(Model.class, converterRegistry, 0.6);


        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(10)
                .bufferSize(1024)
                .open(file);
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                if (row.getRowNum() == 1) {
                    boolean match = operator.match(row);
                    Assert.assertTrue("title match error", match);
                    Map<String, List<TitleDesc>> titles = operator.getTitles(row);
                    Assert.assertSame("titles match error", 12, titles.size());

                } else if (row.getRowNum() == 2) {
                    Model model = operator.convert(row);
                    Assert.assertSame("lineNum integer support", 3, model.getLineNum());
                    Assert.assertEquals("Text One toSingleByte support", "Text", model.getText1());
                    Assert.assertEquals("Text Two string support", "Text", model.getText2());
                    Assert.assertEquals("Text Reorder string support", "1/Ac/Tex/Text/重排", model.getText3());

                    Assert.assertSame("Number int support", 10, model.getIntNum());
                    Assert.assertSame("Number long support", 10L, model.getLongNum());
                    Assert.assertTrue("Number float support", 10f - model.getFloatNum() < 0.01f);
                    Assert.assertTrue("Number double support", 10.0d - model.getDoubleNum() < 0.01d);
                    Assert.assertTrue("Number BigDecimal support", BigDecimal.TEN.compareTo(model.getBigDecimal()) == 0);

                    Assert.assertEquals("'Number Null' Integer support", null, model.getIntNumNull());

                    Assert.assertEquals("LocalDate support", LocalDate.of(2017, 1, 1), model.getLocalDate1());
                    Assert.assertEquals("LocalDate support", LocalDate.of(2017, 1, 1), model.getLocalDate2());
                    Assert.assertEquals("LocalDate support", null, model.getLocalDate3());

                    Assert.assertSame("'Map \\d+'  support", 4, model.getIntKeyMap().size());
                    Assert.assertSame("'Map [A-Z]' support", 3, model.getStringKeyMap().size());
                    Assert.assertSame("'Map 1 (Chinese Name)' support", 2, model.getIntKeyMap2().size());
                    Assert.assertSame("'Map \\d+ (Chinese Name)' support", 4, model.getIntKeyMap3().size());

                } else {
                    continue;
                }
            }
        }

    }

}
