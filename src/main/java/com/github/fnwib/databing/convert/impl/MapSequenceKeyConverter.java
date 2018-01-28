package com.github.fnwib.databing.convert.impl;

import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class MapSequenceKeyConverter implements PropertyConverter {

    private Property                        property;
    private int                             titlesSize;
    private Map<Sequence, SingleConverter> singleConverters;

    public MapSequenceKeyConverter(Property property,
                                    List<CellTitle> titles,
                                    List<ValueHandler> valueHandlers) {
        this.property = property;
        this.titlesSize = titles.size();
        this.singleConverters = Maps.newHashMapWithExpectedSize(titles.size());
        for (CellTitle title : titles) {
            SingleConverter converter = new SingleConverter(property, property.getContentType(), title, valueHandlers);
            singleConverters.put(title.getSequence(), converter);
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
    public Map<Sequence, String> getValue(Row row) {
        if (!isMatched()) {
            return Collections.emptyMap();
        }
        Map<Sequence, String> map = Maps.newHashMapWithExpectedSize(singleConverters.size());
        singleConverters.forEach((cellTitle, converter) -> {
            String value = converter.getValue(row);
            map.put(cellTitle, value);
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
            Map<Sequence, Object> objects = (Map<Sequence, Object>) value;
            if (titlesSize < objects.size()) {
                throw new SettingException("参数长度大于可写入数据长度");
            }
            List<CellText> list = Lists.newArrayListWithCapacity(titlesSize);
            objects.forEach((sequence, obj) -> {
                Optional<CellText> optional = singleConverters.get(sequence).getSingleCellText(obj);
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
