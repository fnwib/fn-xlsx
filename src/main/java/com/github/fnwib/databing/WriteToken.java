package com.github.fnwib.databing;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.ser.Serializer;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@Slf4j
public class WriteToken {

    private final JavaType        javaType;
    private final Method          readMethod;
    private final List<CellTitle> titles;
    private final Serializer      serializer;

    public WriteToken(Property property) {
        this.javaType = property.getJavaType();
        this.readMethod = property.getReadMethod();
        this.titles = Collections.emptyList();
        this.serializer = null;
    }

    public WriteToken(Property property, List<CellTitle> titles) {
        this.javaType = property.getJavaType();
        this.readMethod = property.getReadMethod();
        this.titles = titles;
        JavaType contentType;
        if (javaType.isMapLikeType()) {
            contentType = javaType.getContentType();
        } else if (javaType.isCollectionLikeType()) {
            contentType = javaType.getContentType();
        } else {
            contentType = javaType;
        }
        this.serializer = Context.INSTANCE.findSerializer(contentType);
    }

    public <T> List<CellText> getCellText(T element) {
        try {
            Object value = readMethod.invoke(element);
            if (value == null) {
                return Collections.emptyList();
            }
            if (javaType.isCollectionLikeType()) {
                return collect(value);
            } else if (javaType.isMapLikeType()) {
                return map(value);
            } else {
                CellTitle title = titles.get(0);
                CellText cellText = new CellText(title.getCellNum(), value.toString());
                return Lists.newArrayList(cellText);
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("invoke error ", e);
            return Collections.emptyList();
        }
    }


    public List<CellText> collect(Object value) {
        Collection<Object> objects = (Collection<Object>) value;
        List<CellText> cellTexts = Lists.newArrayListWithCapacity(objects.size());
        if (titles.size() < objects.size()) {
            throw new SettingException("参数长度大于可写入数据长度");
        }
        int i = 0;
        for (Object object : objects) {
            CellTitle title = titles.get(i);
            CellText cellText = new CellText(title.getCellNum(), valueToString(object));
            cellTexts.add(cellText);
            i++;
        }
        return Collections.emptyList();
    }

    public List<CellText> map(Object value) {
        List<CellText> arrayList = Lists.newArrayList();
        Map<Integer, Object> objects = (Map<Integer, Object>) value;
        objects.forEach((k, v) -> {
            CellText cellText = new CellText(k, valueToString(v));
            arrayList.add(cellText);
        });
        return arrayList;
    }

    public String valueToString(Object value) {
        if (value == null) return null;
        if (serializer != null) {
            return serializer.serialize(Optional.of(value));
        }
        return value.toString();
    }
}
