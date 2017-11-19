package com.github.fnwib.convert;

import com.github.fnwib.exception.ParseException;
import com.github.fnwib.operation.Title;
import com.github.fnwib.operation.TitleDesc;
import com.github.fnwib.util.NumberUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

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
        public T convert(Title title, Row row) throws ParseException {
            List<TitleDesc> list = title.getList();
            if (list.size() != 1) {
                return null;
            }
            TitleDesc titleDesc = list.get(0);
            Cell cell = row.getCell(titleDesc.getIndex());
            if (cell ==null){
                return null;
            }
            double value = cell.getNumericCellValue();
            return NumberUtils.convertNumberToTargetClass(value, this.targetType);
        }

    }
}