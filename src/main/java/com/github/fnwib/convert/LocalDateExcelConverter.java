package com.github.fnwib.convert;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.operation.Title;
import com.github.fnwib.operation.TitleDesc;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

public class LocalDateExcelConverter implements ExcelConverter<LocalDate> {

    @Override
    public LocalDate convert(Title title, Row row) throws ExcelException {
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
            case NUMERIC:
                Date date = cell.getDateCellValue();
                Instant instant = date.toInstant();
                return instant.atZone(ZoneId.systemDefault()).toLocalDate();
            case STRING:
                throw new ExcelException(title.getExcelTitles() + " [" + cell.getStringCellValue() + "] not date type");
            case BOOLEAN:
            case FORMULA:
                throw new NotSupportedException(title.getExcelTitles() + " [" + cell.getStringCellValue() + "] not support boolean|formula");
            case _NONE:
            case ERROR:
            default:
                throw new NotSupportedException(title.getExcelTitles() + " [" + cell.getStringCellValue() + "] unknown type");

        }
    }
}