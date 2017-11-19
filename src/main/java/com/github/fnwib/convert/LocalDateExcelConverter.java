package com.github.fnwib.convert;

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
    public LocalDate convert(Title title, Row row) {
        List<TitleDesc> list = title.getList();
        if (list.size() != 1) {
            return null;
        }
        TitleDesc titleDesc = list.get(0);
        Cell cell = row.getCell(titleDesc.getIndex());
        if (cell == null) {
            return null;
        }
        Date date = cell.getDateCellValue();
        Instant instant = date.toInstant();
        return instant.atZone(ZoneId.systemDefault()).toLocalDate();
    }
}