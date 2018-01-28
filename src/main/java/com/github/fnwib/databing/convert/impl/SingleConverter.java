package com.github.fnwib.databing.convert.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.deser.CellDeserializer;
import com.github.fnwib.databing.ser.Serializer;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class SingleConverter implements PropertyConverter {

    private       Property                   property;
    private       CellTitle                  cellTitle;
    private       List<ValueHandler<String>> valueHandlers;
    private final CellDeserializer<?>        deserializer;
    private final Serializer                 serializer;

    public SingleConverter(Property property,
                           CellTitle cellTitle,
                           List<ValueHandler<String>> valueHandlers) {
        this.property = property;
        this.cellTitle = cellTitle;
        this.valueHandlers = valueHandlers;
        this.deserializer = Context.INSTANCE.findCellDeserializer(property.getJavaType());
        this.serializer = Context.INSTANCE.findSerializer(property.getJavaType());
    }

    public SingleConverter(Property property,
                           JavaType contentType,
                           CellTitle cellTitle,
                           List<ValueHandler<String>> valueHandlers) {
        this.property = property;
        this.cellTitle = cellTitle;
        this.valueHandlers = valueHandlers;
        this.deserializer = Context.INSTANCE.findCellDeserializer(contentType);
        this.serializer = Context.INSTANCE.findSerializer(contentType);
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
    public String getValue(Row row) {
        if (!isMatched()) {
            return null;
        }
        Cell cell = row.getCell(cellTitle.getCellNum());
        if (deserializer != null) {
            Object deserialize = deserializer.deserialize(cell);
            return deserialize != null ? deserialize.toString() : null;
        }
        if (cell == null) return null;
        switch (cell.getCellTypeEnum()) {
            case BLANK:
                return null;
            case NUMERIC:
                return cell.getStringCellValue();
            case STRING:
                return ValueUtil.getCellValue(cell, valueHandlers);
            case BOOLEAN:
            case FORMULA:
            case _NONE:
            case ERROR:
            default:
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
                return Collections.emptyList();
            }
            Optional<CellText> optional = getSingleCellText(value);
            return Lists.newArrayList(optional.get());
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("invoke error ", e);
            return Collections.emptyList();
        }
    }

    public Optional<CellText> getSingleCellText(Object value) {
        String serialize;
        if (value == null) {
            serialize = null;
        } else if (serializer != null) {
            serialize = serializer.serialize(Optional.of(value));
        } else {
            serialize = value.toString();
        }
        CellText cellText = new CellText(cellTitle.getCellNum(), serialize);
        return Optional.of(cellText);
    }

}
