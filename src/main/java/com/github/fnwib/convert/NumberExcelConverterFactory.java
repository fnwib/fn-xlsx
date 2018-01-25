package com.github.fnwib.convert;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.parse.Title;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.util.NumberFormatUtils;
import com.github.fnwib.write.CellText;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
@Deprecated
public class NumberExcelConverterFactory implements ExcelConverterFactory<Number> {

    @Override
    public <T extends Number> ExcelConverter<T> getConverter(JavaType targetType) {
        return new NumberExcelConverterFactory.NumberConverter(targetType.getRawClass());
    }

    private static class NumberConverter<T extends Number> implements ExcelConverter<T> {

        private final Class<T> targetType;


        private static final NumberFormat NUMBER_FORMAT = NumberFormat.getInstance(Locale.CHINA);

        static {
            NUMBER_FORMAT.setGroupingUsed(false);
        }

        public NumberConverter(Class<T> targetType) {
            this.targetType = targetType;
        }

        @Override
        public T getDefaultValue() {
            return null;
        }

        @Override
        public T convert(Title title, Row row) throws ExcelException {
            List<TitleDesc> list = title.getList();
            if (list.size() != 1) {
                return null;
            }
            TitleDesc titleDesc = list.get(0);
            Cell cell = row.getCell(titleDesc.getIndex());
            if (cell == null) {
                return null;
            }
            switch (cell.getCellTypeEnum()) {
                case BLANK:
                    return null;
                case STRING:
                    String cellValue = cell.getStringCellValue().trim();
                    if (NumberUtils.isParsable(cellValue)) {
                        try {
                            Number parse = NumberFormat.getInstance().parse(cellValue);
                            return NumberFormatUtils.convertNumberToTargetClass(parse, this.targetType);
                        } catch (java.text.ParseException e) {
                            throw new ExcelException(title.getExcelTitles() + " [" + cellValue + "] can't parse number");
                        }
                    } else {
                        throw new ExcelException(title.getExcelTitles() + " [" + cellValue + "] can't parse number");
                    }
                case NUMERIC:
                    double value = cell.getNumericCellValue();
                    return NumberFormatUtils.convertNumberToTargetClass(value, this.targetType);
                case BOOLEAN:
                case FORMULA:
                    throw new NotSupportedException(title.getExcelTitles() + " [" + cell.getStringCellValue() + "] not support boolean|formula");
                case _NONE:
                case ERROR:
                default:
                    throw new NotSupportedException(title.getExcelTitles() + " [" + cell.getStringCellValue() + "] unknown type ");

            }
        }

        @Override
        public List<CellText> writeValue(Object obj, Title title) throws ExcelException {
            TitleDesc desc = title.getList().get(0);
            return Arrays.asList(new CellText(desc.getIndex(), obj == null ? null : NUMBER_FORMAT.format(obj)));
        }
    }
}