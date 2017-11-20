package com.github.fnwib.convert;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.operation.Title;
import com.github.fnwib.operation.TitleDesc;
import com.github.fnwib.util.NumberFormatUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.text.NumberFormat;
import java.util.List;

public class NumberExcelConverterFactory implements ExcelConverterFactory<Number> {

    @Override
    public <T extends Number> ExcelConverter<T> getConverter(Class<T> targetType) {
        return new NumberExcelConverterFactory.NumberConverter(targetType);
    }

    private static class NumberConverter<T extends Number> implements ExcelConverter<T> {

        private final Class<T> targetType;

        public NumberConverter(Class<T> targetType) {
            this.targetType = targetType;
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
                    String cellValue = cell.getStringCellValue();
                    try {
                        if (NumberUtils.isParsable(cellValue)) {
                            Number parse = NumberFormat.getInstance().parse(cellValue);
                            return NumberFormatUtils.convertNumberToTargetClass(parse, this.targetType);
                        }
                    } catch (java.text.ParseException e) {
                        throw new ExcelException(title.getExcelTitles() + " [" + cellValue + "] can't parse number");
                    }
                    break;
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
            return null;
        }

    }
}