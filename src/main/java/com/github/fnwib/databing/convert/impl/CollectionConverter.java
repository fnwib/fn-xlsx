package com.github.fnwib.databing.convert.impl;

import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
public class CollectionConverter implements PropertyConverter {

    private Property              property;
    private int                   titlesSize;
    private List<SingleConverter> singleConverters;

    public CollectionConverter(Property property,
                               List<CellTitle> titles,
                               List<ValueHandler<String>> valueHandlers) {
        this.property = property;
        this.titlesSize = titles.size();
        this.singleConverters = Lists.newArrayListWithCapacity(titles.size());
        for (CellTitle title : titles) {
            SingleConverter converter = new SingleConverter(property, title, valueHandlers);
            singleConverters.add(converter);
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
    public Collection<String> getValue(Row row) {
        if (!isMatched()) {
            return Collections.emptyList();
        }
        Collection<String> list = Lists.newArrayListWithCapacity(singleConverters.size());
        for (SingleConverter converter : singleConverters) {
            String value = converter.getValue(row);
            list.add(value);
        }
        return list;
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
            Collection<Object> objects = (Collection<Object>) value;
            if (titlesSize < objects.size()) {
                throw new SettingException("参数长度大于可写入数据长度");
            }
            List<CellText> cellTexts = Lists.newArrayListWithCapacity(objects.size());
            int i = 0;
            for (Object object : objects) {
                SingleConverter converter = singleConverters.get(i);
                Optional<CellText> optional = converter.getSingleCellText(object);
                if (optional.isPresent()) {
                    cellTexts.add(optional.get());
                }
                i++;
            }
            return cellTexts;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("invoke error ", e);
            return Collections.emptyList();
        }
    }

}
