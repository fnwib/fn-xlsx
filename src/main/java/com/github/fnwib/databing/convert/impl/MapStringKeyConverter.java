package com.github.fnwib.databing.convert.impl;

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

public class MapStringKeyConverter implements PropertyConverter {

    private static final Logger log = LoggerFactory.getLogger(MapStringKeyConverter.class);

    private Property                   property;
    private int                        titlesSize;
    private Map<String, BeanConverter> converters;

    public MapStringKeyConverter(Property property,
                                 List<CellTitle> titles,
                                 Collection<ValueHandler> valueHandlers) {
        this.property = property;
        this.titlesSize = titles.size();
        this.converters = Maps.newHashMapWithExpectedSize(titles.size());
        for (CellTitle title : titles) {
            BeanConverter converter = new BeanConverter(property, property.getContentType(), title, valueHandlers);
            converters.put(title.getText(), converter);
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
    public Map<String, String> getValue(Row row) {
        if (!isMatched()) {
            return Collections.emptyMap();
        }
        Map<String, String> map = Maps.newHashMapWithExpectedSize(converters.size());
        converters.forEach((titleName, converter) -> {
            String value = converter.getValue(row);
            map.put(titleName, value);
        });
        return map;
    }

    @Override
    public <T> List<CellText> getCellText(T element) {
        if (!isMatched()) {
            return Collections.emptyList();
        }
        try {
            Object value = property.getReadMethod().invoke(element);
            if (value == null) {
                return Collections.emptyList();
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
                if (optional.isPresent()) {
                    list.add(optional.get());
                }
            });
            return list;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("invoke error ", e);
            return Collections.emptyList();
        }
    }

}
