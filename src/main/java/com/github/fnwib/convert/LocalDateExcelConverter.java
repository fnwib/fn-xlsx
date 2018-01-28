package com.github.fnwib.convert;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.parse.Title;
import com.github.fnwib.parse.TitleDesc;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
@Deprecated
public class LocalDateExcelConverter implements ExcelConverter<LocalDate> {

    private DateTimeFormatter dateTimeFormatter;

    private static final Pattern SHORT_DATE_PATTERN_LINE = Pattern.compile("^\\d{4}-\\d{2}-\\d{2}$");

    private static final Pattern SHORT_DATE_PATTERN_SLASH = Pattern.compile("^\\d{4}/\\d{1,2}/\\d{1,2}$");

    private static final Pattern SHORT_DATE_PATTERN_DOUBLE_SLASH = Pattern.compile("^\\d{4}\\\\\\d{2}\\\\\\d{2}$");

    private static final Pattern SHORT_DATE_PATTERN_NONE = Pattern.compile("^\\d{4}\\d{2}\\d{2}$");

    private final List<ValueHandler> valueHandlers;

    public LocalDateExcelConverter() {
        this.valueHandlers = Collections.emptyList();
    }

    public LocalDateExcelConverter(List<ValueHandler> valueHandlers) {
        this.valueHandlers = valueHandlers;
    }

    public LocalDateExcelConverter(ValueHandler... valueHandlers) {
        this.valueHandlers = Lists.newArrayList(valueHandlers);
    }


    @Override
    public LocalDate getDefaultValue() {
        return null;
    }

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
                String value = ValueUtil.getCellValue(cell, valueHandlers);
                try {
                    if (dateTimeFormatter == null) {
                        DateTimeFormatter formatter = getDateTimeFormatter(value);
                        if (formatter == null) {
                            throw new ExcelException(title.getExcelTitles() + " [" + cell.getStringCellValue() + "] 不支持文本转日期");
                        }
                        this.dateTimeFormatter = formatter;
                    }
                    return LocalDate.parse(value, dateTimeFormatter);
                } catch (DateTimeParseException e) {
                    DateTimeFormatter formatter = getDateTimeFormatter(value);
                    if (formatter == null) {
                        throw new ExcelException(title.getExcelTitles() + " [" + cell.getStringCellValue() + "] 不支持文本转日期");
                    }
                    this.dateTimeFormatter = formatter;
                    return LocalDate.parse(value, formatter);
                }
            case BOOLEAN:
            case FORMULA:
                throw new NotSupportedException(title.getExcelTitles() + " [" + cell.getStringCellValue() + "] not support boolean|formula");
            case _NONE:
            case ERROR:
            default:
                throw new NotSupportedException(title.getExcelTitles() + " [" + cell.getStringCellValue() + "] unknown type");

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

    @Override
    public List<CellText> writeValue(Object obj, Title title) throws ExcelException {
        LocalDate localDate = (LocalDate) obj;
        TitleDesc desc = title.getList().get(0);
        return Arrays.asList(new CellText(desc.getIndex(), obj == null ? null : localDate.toString()));
    }

}