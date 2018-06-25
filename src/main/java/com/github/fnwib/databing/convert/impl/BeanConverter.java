package com.github.fnwib.databing.convert.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.deser.CellDeserializer;
import com.github.fnwib.databing.ser.Serializer;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.util.ExcelUtil;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BeanConverter implements PropertyConverter {

    private static final Logger log = LoggerFactory.getLogger(BeanConverter.class);

    private final Property                 property;
    private final CellTitle                cellTitle;
    private final Collection<ValueHandler> valueHandlers;
    private final CellDeserializer<?>      deserializer;
    private final Serializer               serializer;
    private final CellText                 emptyCellText;

    public BeanConverter(Property property,
                         CellTitle cellTitle,
                         Collection<ValueHandler> valueHandlers) {
        this(property, property.getJavaType(), cellTitle, valueHandlers);
    }

    public BeanConverter(Property property,
                         JavaType contentType,
                         CellTitle cellTitle,
                         Collection<ValueHandler> valueHandlers) {
        this.property = property;
        this.cellTitle = cellTitle;
        this.valueHandlers = valueHandlers;
        this.deserializer = Context.INSTANCE.findCellDeserializer(contentType);
        this.serializer = Context.INSTANCE.findSerializer(contentType);
        this.emptyCellText = new CellText(cellTitle.getCellNum(), "");
    }

    @Override
    public boolean isMatched() {
        return cellTitle != null;
    }

    @Override
    public String getKey() {
        return property.getName();
    }


    @Override
    public Optional<String> getValue(Row row) {
        if (!isMatched()) {
            return Optional.empty();
        }
        Cell cell = row.getCell(cellTitle.getCellNum());
        if (deserializer != null) {
            Object deserialize = deserializer.deserialize(cell);
            if (deserialize == null) {
                return Optional.empty();
            } else {
                return Optional.of(deserialize.toString());
            }
        }
        if (cell == null) {
            return Optional.empty();
        }
        switch (cell.getCellTypeEnum()) {
            case BLANK:
                return Optional.empty();
            case NUMERIC:
                return Optional.of(cell.getStringCellValue());
            case STRING:
                return ValueUtil.getCellValue(cell, valueHandlers);
            case ERROR:
            case BOOLEAN:
            case FORMULA:
            case _NONE:
                String format = String.format("坐标[%s][%s]值为[%s],类型是[%s]",
                        row.getRowNum() + 1,
                        ExcelUtil.num2Column(cell.getColumnIndex() + 1),
                        cell.getStringCellValue(),
                        cell.getCellTypeEnum().name());
                throw new ExcelException(format);
            default:
                log.error("-> cell title  [{}]", cellTitle);
                log.error("-> row num [{}]", row.getRowNum());
                log.error("-> cell column name [{}]", ExcelUtil.num2Column(cell.getColumnIndex() + 1));
                log.error("-> cell string value [{}]", cell.getStringCellValue());
                log.error("-> cell type  [{}]", cell.getCellTypeEnum().name());
                throw new NotSupportedException(" [" + cell.getStringCellValue() + "] unknown type");
        }
    }

    @Override
    public <Param> List<CellText> getCellText(Param element) {
        if (!isMatched() || element == null) {
            return Collections.emptyList();
        }
        try {
            Object value = property.getReadMethod().invoke(element);
            if (value == null) {
                return Lists.newArrayList(emptyCellText);
            }
            Optional<CellText> optional = getSingleCellText(value);
            return Lists.newArrayList(optional.orElseThrow(SettingException::new));
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("invoke error ", e);
            return Collections.emptyList();
        }
    }

    public Optional<CellText> getSingleCellText(Object value) {
        if (value == null) {
            return Optional.of(emptyCellText);
        }
        String serialize;
        if (serializer != null) {
            serialize = serializer.serialize(Optional.of(value));
        } else {
            serialize = value.toString();
        }
        CellText cellText = new CellText(cellTitle.getCellNum(), serialize);
        return Optional.of(cellText);
    }

}
