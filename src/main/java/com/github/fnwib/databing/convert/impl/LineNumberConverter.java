package com.github.fnwib.databing.convert.impl;

import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

@Slf4j
public class LineNumberConverter implements PropertyConverter {

    private final String    name;
    private final Method    readMethod;
    private final CellTitle title;

    public LineNumberConverter(Property property, CellTitle title) {
        this.title = title;
        this.name = property.getName();
        this.readMethod = property.getReadMethod();
    }

    @Override
    public boolean isMatched() {
        return title != null;
    }

    @Override
    public String getKey() {
        return name;
    }

    @Override
    public Object getValue(Row row) {
        return row.getRowNum();
    }

    @Override
    public <T> List<CellText> getCellText(T element) {
        if (title != null) {
            try {
                Object value = readMethod.invoke(element);
                if (value == null) {
                    return Collections.emptyList();
                }
                return Lists.newArrayList();
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("invoke error ", e);
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }


    }


}
