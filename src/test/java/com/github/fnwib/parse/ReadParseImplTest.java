package com.github.fnwib.parse;

import com.github.fnwib.convert.*;
import com.github.fnwib.read.ReadParser;
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
import java.util.Map;

public class ReadParseImplTest {

    private File file;

    @Before
    public void initDate() {
        ClassLoader classLoader = getClass().getClassLoader();
        file = new File(classLoader.getResource("test-file/test.xlsx").getFile());
    }

    @Test
    public void convert() {
        ExcelGenericConversionService converterRegistry = new ExcelGenericConversionService();
        converterRegistry.addConverter(String.class, new StringExcelConverter());
        converterRegistry.addConverter(LocalDate.class, new LocalDateExcelConverter());
        converterRegistry.addConverter(Map.class, new SeqKeyMapExcelConverter());
        converterRegistry.addConverterFactory(Number.class, new NumberExcelConverterFactory());
        Parser<Model> parser = new ParseImpl<>(Model.class, converterRegistry, 0.6);


        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(10)
                .bufferSize(1024)
                .open(file);
        for (Sheet sheet : workbook) {
            for (Row row : sheet) {
                if (row.getRowNum() == 0) {
                    boolean match = parser.match(row);
                    Assert.assertTrue("title match error", match);
                } else if (row.getRowNum() == 1) {
                    ReadParser<Model> readParser = parser.createReadParser();
                    Model model = readParser.convert(row);
                    Assert.assertSame("lineNum integer support", 2, model.getLineNum());
                    Assert.assertEquals("Text One toSingleByte support", "Text", model.getText1());
                    Assert.assertEquals("Text Two string support", "Text", model.getText2());
                    Assert.assertEquals("Text Reorder string support", "1/Ac/Tex/Text/重排", model.getText3());

                    Assert.assertSame("Number int support", 10, model.getIntNum());
                    Assert.assertSame("Number long support", 10L, model.getLongNum());
                    Assert.assertTrue("Number float support", 10f - model.getFloatNum() < 0.01f);
                    Assert.assertTrue("Number double support", 10.0d - model.getDoubleNum() < 0.01d);
                    Assert.assertTrue("Number BigDecimal support", BigDecimal.TEN.compareTo(model.getBigDecimal()) == 0);

                    Assert.assertEquals("'Number Null' Integer support", null, model.getIntNumNull());

                    Assert.assertEquals("LocalDate support data1", LocalDate.of(2017, 1, 1), model.getLocalDate1());
                    Assert.assertEquals("LocalDate support data2", LocalDate.of(2017, 1, 1), model.getLocalDate2());
                    Assert.assertEquals("LocalDate support null", null, model.getLocalDate3());

                    Assert.assertEquals("LocalDate 'yyyy-MM-dd'support", LocalDate.of(2017, 1, 1), model.getLocalDate4());
                    Assert.assertEquals("LocalDate 'yyyy/MM/dd'support", LocalDate.of(2017, 1, 1), model.getLocalDate5());
                    Assert.assertEquals("LocalDate 'yyyy/MM/dd2'support", LocalDate.of(2017, 1, 1), model.getLocalDate8());
                    Assert.assertEquals("LocalDate 'yyyy\\MM\\dd'support", LocalDate.of(2017, 1, 1), model.getLocalDate6());
                    Assert.assertEquals("LocalDate 'yyyyMMdd'support", LocalDate.of(2017, 1, 1), model.getLocalDate7());

                    Assert.assertSame("'Map \\d+'  support", 4, model.getIntKeyMap().size());
                    Assert.assertSame("'Map [A-Z]' support", 3, model.getStringKeyMap().size());
                    Assert.assertSame("'Map 1 (Chinese Name)' support", 2, model.getIntKeyMap2().size());
                    Assert.assertSame("'Map \\d+ (Chinese Name)' support", 4, model.getIntKeyMap3().size());

                }
            }
        }

    }

}
