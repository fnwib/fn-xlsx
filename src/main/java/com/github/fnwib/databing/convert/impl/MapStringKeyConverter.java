package com.github.fnwib.databing.convert.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;

public class MapStringKeyConverter implements PropertyConverter {

    private static final Logger log = LoggerFactory.getLogger(MapStringKeyConverter.class);

    private final Property                   property;
    private final int                        titlesSize;
    private final Map<String, BeanConverter> converters;
    private final List<CellText>             emptyCellTexts;

    public MapStringKeyConverter(Property property,
                                 List<CellTitle> titles,
                                 Collection<ValueHandler> valueHandlers) {
        checkTitle(titles, property.getJavaType());
        this.property = property;
        this.titlesSize = titles.size();
        this.converters = Maps.newHashMapWithExpectedSize(titles.size());
        this.emptyCellTexts = Lists.newArrayListWithCapacity(titles.size());
        for (CellTitle title : titles) {
            BeanConverter converter = new BeanConverter(property, property.getContentType(), title, valueHandlers);
            converters.put(title.getText(), converter);
            emptyCellTexts.add(new CellText(title.getCellNum(), ""));
        }
    }

    private void checkTitle(List<CellTitle> titles, JavaType javaType) {
        Map<String, List<CellTitle>> titleNames = titles.stream().collect(Collectors.groupingBy(CellTitle::getText));
        titleNames = Maps.filterValues(titleNames, v -> v.size() > 1);
        if (titleNames.size() > 0) {
            log.error("-> property is [{}] ,type is [{}]", property.getName(), javaType);
            titleNames.forEach((titleName, sameNameTitles) -> log.error("-> 存在相同名称的tile[{}]", sameNameTitles));
            throw new SettingException("Map类型的key是String(title name)匹配到title存在相同的名称");
        }
    }

    @Override
    public boolean isMatched() {
        return titlesSize > 0;
    }


    @Override
    public String getKey() {
        return property.getName();
    }

    @Override
    public Optional<Map<String, String>> getValue(Row row) {
        if (!isMatched()) {
            return Optional.of(Collections.emptyMap());
        }
        Map<String, String> map = Maps.newHashMapWithExpectedSize(converters.size());
        converters.forEach((titleName, converter) -> {
            if (converter.isMatched()) {
                converter.getValue(row).ifPresent(value -> map.put(titleName, value));
            }
        });
        return Optional.of(map);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> List<CellText> getCellText(T element) {
        if (!isMatched()) {
            return emptyCellTexts;
        }
        try {
            Object value = property.getReadMethod().invoke(element);
            if (value == null) {
                return emptyCellTexts;
            }
            Map<String, Object> objects = (Map<String, Object>) value;
            if (titlesSize < objects.size()) {
                log.error("-->property name is {} , matched tile size is [{}] , value is [{}]",
                        property.getName(), titlesSize, element);
                throw new SettingException("参数长度大于可写入数据长度");
            }
            List<CellText> list = Lists.newArrayListWithCapacity(titlesSize);
            objects.forEach((titleName, obj) -> {
                Optional<CellText> optional = converters.get(titleName).getSingleCellText(obj);
                optional.ifPresent(cellText -> list.add(cellText));
            });
            return list;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("invoke error ", e);
            return emptyCellTexts;
        }
    }

}
