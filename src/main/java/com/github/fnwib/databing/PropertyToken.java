package com.github.fnwib.databing;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.deser.CellDeserializer;
import com.github.fnwib.databing.ser.Serializer;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.CellText;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public final class PropertyToken {
    private final String                     name;
    private final JavaType                   javaType;
    private final AutoMapping                autoMapping;
    private final List<CellTitle>            titles;
    private final List<ValueHandler<String>> valueHandlers;
    private final CellDeserializer<?>        cellDeserializer;

    public PropertyToken(Property property) {
        this.name = property.getName();
        this.javaType = property.getJavaType();
        this.autoMapping = property.getAnnotation(AutoMapping.class);
        this.titles = Collections.emptyList();
        this.valueHandlers = Collections.emptyList();
        this.cellDeserializer = null;
    }

    public PropertyToken(Property property,
                         List<CellTitle> titles,
                         List<ValueHandler<String>> valueHandlers) {
        this.name = property.getName();
        this.javaType = property.getJavaType();
        this.autoMapping = property.getAnnotation(AutoMapping.class);
        this.titles = titles;
        this.valueHandlers = valueHandlers;
        JavaType contentType;
        if (javaType.isMapLikeType() || javaType.isCollectionLikeType()) {
            contentType = javaType.getContentType();
        } else {
            contentType = javaType;
        }
        CellDeserializer<?> cellDeserializer = Context.INSTANCE.findCellDeserializer(contentType);
        this.cellDeserializer = cellDeserializer;
    }

    public boolean isMatchEmpty() {
        return titles.isEmpty();
    }

    public String getMapKey() {
        return name;
    }

    public Object getMapValue(Row row) {
        if (autoMapping.operation() == Operation.LINE_NUM) {
            return row.getRowNum();
        } else {
            if (javaType.isCollectionLikeType()) {
                return toCollection(row);
            } else if (javaType.isMapLikeType()) {
                if (javaType.getKeyType().getRawClass() == String.class) {
                    return toStringKeyMap(row);
                } else {
                    return toIntegerKeyMap(row);
                }
            } else {
                if (titles.isEmpty()) {
                    return null;
                } else if (titles.size() == 1) {
                    int cellNum = titles.get(0).getCellNum();
                    Cell cell = row.getCell(cellNum);
                    return toString(cell);
                } else {
                    String join = Joiner.on(",").join(titles);
                    throw new SettingException(name + "匹配到多列" + join);
                }
            }
        }
    }

    private List<Cell> getCell(Row row) {
        if (titles.isEmpty()) {
            return Collections.emptyList();
        }
        List<Cell> cells = new ArrayList<>(titles.size());
        for (CellTitle title : titles) {
            cells.add(row.getCell(title.getCellNum()));
        }
        return cells;
    }

    private Collection<String> toCollection(Row row) {
        if (titles.isEmpty()) {
            return Collections.emptyList();
        }
        Collection<String> list = Lists.newArrayListWithCapacity(titles.size());
        for (CellTitle title : titles) {
            Cell cell = row.getCell(title.getCellNum());
            list.add(toString(cell));
        }
        return list;
    }

    private Map<Integer, String> toIntegerKeyMap(Row row) {
        if (titles.isEmpty()) {
            return Collections.emptyNavigableMap();
        }
        Map<Integer, String> map = Maps.newHashMapWithExpectedSize(titles.size());
        for (CellTitle cellText : titles) {
            Cell cell = row.getCell(cellText.getCellNum());
            map.put(cellText.getCellNum(), toString(cell));
        }
        return map;
    }

    private Map<String, String> toStringKeyMap(Row row) {
        if (titles.isEmpty()) {
            return Collections.emptyNavigableMap();
        }
        Map<String, String> map = Maps.newHashMapWithExpectedSize(titles.size());
        for (CellTitle title : titles) {
            Cell cell = row.getCell(title.getCellNum());
            map.put(title.getText(), toString(cell));
        }
        return map;
    }

    private String toString(Cell cell) {
        if (cellDeserializer != null) {
            Object deserialize = cellDeserializer.deserialize(cell);
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


}
