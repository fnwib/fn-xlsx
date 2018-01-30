package com.github.fnwib.databing.convert.impl;

import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LineNumberConverter implements PropertyConverter {

    private static final Logger log = LoggerFactory.getLogger(LineNumberConverter.class);

    private final String    name;
    private final Method    readMethod;
    private final CellTitle title;

    public LineNumberConverter(Property property, List<CellTitle> titles) {
        check(property);
        final CellTitle title;
        if (titles.isEmpty()) {
            title = null;
        } else {
            title = titles.get(0);
        }
        this.title = title;
        this.name = property.getName();
        this.readMethod = property.getReadMethod();
    }

    void check(Property property) {
        if (property.getJavaType().getRawClass() != Integer.class) {
            String format = String.format("property %s 类型应该是 %s ", property.getName(), Integer.class);
            log.error("--> error ", format);
            throw new SettingException(format);
        }
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
    public Optional<Integer> getValue(Row row) {
        return Optional.of(row.getRowNum() + 1);
    }

    @Override
    public <T> List<CellText> getCellText(T element) {
        if (title != null) {
            try {
                Object value = readMethod.invoke(element);
                if (value == null) {
                    return Lists.newArrayList(title.getEmptyCellText());
                }
                return Lists.newArrayList(new CellText(title.getCellNum(), value.toString()));
            } catch (IllegalAccessException | InvocationTargetException e) {
                log.error("invoke error ", e);
                return Collections.emptyList();
            }
        } else {
            return Collections.emptyList();
        }


    }


}
