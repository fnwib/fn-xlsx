package com.github.fnwib.databing.convert.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.title.Sequence;
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

public class MapSequenceKeyConverter implements PropertyConverter {

    private static final Logger log = LoggerFactory.getLogger(MapSequenceKeyConverter.class);


    private final Property                     property;
    private final int                          titlesSize;
    private final Map<Sequence, BeanConverter> converters;
    private final List<CellText>               emptyCellTexts;

    public MapSequenceKeyConverter(Property property,
                                   List<CellTitle> titles,
                                   Collection<ValueHandler> valueHandlers) {
        checkTitle(titles, property.getJavaType());
        this.property = property;
        this.titlesSize = titles.size();
        this.converters = Maps.newHashMapWithExpectedSize(titles.size());
        this.emptyCellTexts = Lists.newArrayListWithCapacity(titles.size());
        for (CellTitle title : titles) {
            BeanConverter converter = new BeanConverter(property, property.getContentType(), title, valueHandlers);
            converters.put(title.getSequence(), converter);
            emptyCellTexts.add(new CellText(title.getCellNum(), ""));
        }
    }

    void checkTitle(List<CellTitle> titles, JavaType javaType) {
        Map<Sequence, List<CellTitle>> titleNames = titles.stream().collect(Collectors.groupingBy(CellTitle::getSequence));
        titleNames = Maps.filterValues(titleNames, v -> v.size() > 1);
        if (titleNames.size() > 0) {
            log.error("-> property is [{}] ,type is [{}]", property.getName(), javaType);
            titleNames.forEach((titleName, sameNameTitles) -> log.error("-> 存在相同名称的tile[{}]", sameNameTitles));
            throw new SettingException("Map类型的key是Sequence(title sequence)匹配到title存在相同的名称");
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
    public Optional<Map<Sequence, String>> getValue(Row row) {
        if (!isMatched()) {
            return Optional.of(Collections.emptyMap());
        }
        Map<Sequence, String> map = Maps.newHashMapWithExpectedSize(converters.size());
        converters.forEach((cellTitle, converter) -> {
            if (converter.isMatched()) {
                converter.getValue(row).ifPresent(value -> map.put(cellTitle, value));
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
            Map<Sequence, Object> objects = (Map<Sequence, Object>) value;
            if (titlesSize < objects.size()) {
                log.error("-->property name is {} , matched tile size is [{}] , value is [{}]",
                        property.getName(), titlesSize, element);
                throw new SettingException("参数长度大于可写入数据长度");
            }
            List<CellText> list = Lists.newArrayListWithCapacity(titlesSize);
            objects.forEach((sequence, obj) -> {
                Optional<CellText> optional = converters.get(sequence).getSingleCellText(obj);
                if (optional.isPresent()) {
                    list.add(optional.get());
                }
            });
            return list;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("invoke error ", e);
            return emptyCellTexts;
        }
    }

}
