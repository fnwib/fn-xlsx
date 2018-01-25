package com.github.fnwib.read;

import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.ExcelRowParser;
import com.github.fnwib.databing.RowParser;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.databing.deser.LocalDateCellDeserializer;
import com.github.fnwib.util.BCConvert;
import com.monitorjbl.xlsx.StreamingReader;
import model.AutoMappingModel;
import org.apache.poi.ss.usermodel.Workbook;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class ExcelReaderImplTest {

    ExcelReader<AutoMappingModel> reader;

    ValueHandler<String> valueHandler  = (s) -> BCConvert.toSingleByte(s);
    ValueHandler<String> valueHandler2 = (s) -> s.trim();

    @Before
    public void initDate() {
        ClassLoader classLoader = getClass().getClassLoader();
        File file = new File(classLoader.getResource("test-file/test-auto-mapping.xlsx").getFile());
        Workbook workbook = StreamingReader.builder()
                .rowCacheSize(10)
                .bufferSize(1024)
                .open(file);
        Context.INSTANCE.register(new LocalDateCellDeserializer());
        Context.INSTANCE.registerContentValueHandlers(valueHandler, valueHandler2);
//        Context.INSTANCE.registerTitleValueHandlers(valueHandler, valueHandler2);

        RowParser<AutoMappingModel> parser = new ExcelRowParser<>(AutoMappingModel.class);
        reader = new ExcelReaderImpl2<>(parser, workbook, 0);
    }

    @Test
    public void getPreTitle() {
        String preTitle = reader.getPreTitle(0, 0);
        Assert.assertEquals("标题", "标题", preTitle);
    }

    @Test
    public void findTitle() {
        boolean title = reader.findTitle();
        Assert.assertTrue("find title", title);
    }

    @Test
    public void getData() {
        List<AutoMappingModel> data = reader.getData();
        Assert.assertEquals("data size ", 1, data.size());
        for (AutoMappingModel datum : data) {
            check(datum);
        }
    }


    private void check(AutoMappingModel model) {
        Assert.assertSame("lineNum integer support", 2, model.getLineNum());

        Assert.assertEquals("Text One toSingleByte support", "TEXT", model.getText1());
        Assert.assertEquals("Text Two string support", "Text", model.getText2());
        Assert.assertEquals("Text Reorder string support", "1/Ac/Tex/Text/重排", model.getText3());

        Assert.assertSame("Number int support", 10, model.getIntNum());
        Assert.assertSame("Number long support", 10L, model.getLongNum());
        Assert.assertTrue("Number float support", 10f - model.getFloatNum() < 0.01f);
        Assert.assertTrue("Number double support", 10.0d - model.getDoubleNum() < 0.01d);
        Assert.assertTrue("Number BigDecimal support", BigDecimal.TEN.compareTo(model.getBigDecimal()) == 0);

        Assert.assertEquals("'Number Null' Integer support", null, model.getIntNumNull());

        Assert.assertEquals("LocalDate support data", null, model.getLocalDate());
        List<LocalDate> localDateList = model.getLocalDateList();
        Assert.assertSame("'LocalDate size", 6, localDateList.size());
        LocalDate date = LocalDate.of(2017, 1, 1);
        for (LocalDate localDate : localDateList) {
            Assert.assertEquals("LocalDate support data", date, localDate);
        }
        Assert.assertSame("'Map \\d+'  support", 4, model.getIntKeyMap().size());
        Assert.assertSame("'Map [A-Z]' support", 3, model.getStringKeyMap().size());
        Assert.assertSame("'Map 1 (Chinese Name)' support", 2, model.getIntKeyMap2().size());
        Assert.assertSame("'Map \\d+ (Chinese Name)' support", 4, model.getIntKeyMap3().size());
        Assert.assertNotNull("map no match ", model.getNoMatchMap());
}

}
