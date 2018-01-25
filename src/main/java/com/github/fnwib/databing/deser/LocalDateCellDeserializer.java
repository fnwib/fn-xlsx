package com.github.fnwib.databing.deser;

import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.util.ValueUtil;
import org.apache.poi.ss.usermodel.Cell;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.regex.Pattern;

public class LocalDateCellDeserializer implements CellDeserializer<LocalDate> {

    private DateTimeFormatter dateTimeFormatter;

    private static final Pattern SHORT_DATE_PATTERN_LINE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private static final Pattern SHORT_DATE_PATTERN_SLASH = Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2}$");

    private static final Pattern SHORT_DATE_PATTERN_DOUBLE_SLASH = Pattern.compile("^\\d{4}\\\\\\d{2}\\\\\\d{2}$");

    private static final Pattern SHORT_DATE_PATTERN_NONE = Pattern.compile("^\\d{4}\\d{2}\\d{2}$");


    @Override
    public LocalDate deserialize(Cell cell) {
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
                String value = ValueUtil.getCellValue(cell);
                try {
                    if (dateTimeFormatter == null) {
                        DateTimeFormatter formatter = getDateTimeFormatter(value);
                        if (formatter == null) {
                            throw new ExcelException(" [" + cell.getStringCellValue() + "] 不支持文本转日期");
                        }
                        this.dateTimeFormatter = formatter;
                    }
                    return LocalDate.parse(value, dateTimeFormatter);
                } catch (DateTimeParseException e) {
                    DateTimeFormatter formatter = getDateTimeFormatter(value);
                    if (formatter == null) {
                        throw new ExcelException(" [" + cell.getStringCellValue() + "] 不支持文本转日期");
                    }
                    this.dateTimeFormatter = formatter;
                    return LocalDate.parse(value, formatter);
                }
            case BOOLEAN:
            case FORMULA:
                throw new NotSupportedException(" [" + cell.getStringCellValue() + "] not support boolean|formula");
            case _NONE:
            case ERROR:
            default:
                throw new NotSupportedException(" [" + cell.getStringCellValue() + "] unknown type");

        }
    }

    public DateTimeFormatter getDateTimeFormatter(String value) {
        if (SHORT_DATE_PATTERN_LINE.matcher(value).matches()) {
            return DateTimeFormatter.ofPattern("yyyy-MM-dd");
        } else if (SHORT_DATE_PATTERN_SLASH.matcher(value).matches()) {
            return DateTimeFormatter.ofPattern("yyyy/M/d");
        } else if (SHORT_DATE_PATTERN_DOUBLE_SLASH.matcher(value).matches()) {
            return DateTimeFormatter.ofPattern("yyyy\\MM\\dd");
        } else if (SHORT_DATE_PATTERN_NONE.matcher(value).matches()) {
            return DateTimeFormatter.ofPattern("yyyyMMdd");
        } else {
            return null;
        }
    }

}