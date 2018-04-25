package com.github.fnwib.databing.title;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.convert.impl.*;
import com.github.fnwib.databing.title.match.TitleMatcher;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.util.ValueUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * title解析器  将Excel.Row与Entity.Field做映射
 */
public class TitleResolver {

    private static final Logger log = LoggerFactory.getLogger(TitleResolver.class);

    private final Collection<ValueHandler> readContentValueHandlers;
    private final Collection<ValueHandler> titleValueHandlers;

    public TitleResolver(Collection<ValueHandler> readContentValueHandlers, Collection<ValueHandler> titleValueHandlers) {
        this.readContentValueHandlers = readContentValueHandlers;
        this.titleValueHandlers = titleValueHandlers;
    }

    public Set<PropertyConverter> resolve(Class<?> entityClass, Row row) {
        List<CellTitle> cellTitles = getCellTitles(row);
        List<Property> properties = BeanResolver.INSTANCE.getProperties(entityClass);
        Set<PropertyConverter> converters = Sets.newHashSet();
        for (Property property : properties) {
            Optional<TitleMatcher> matcher = property.getTitleMatcher();
            if (matcher.isPresent()) {
                TitleMatcher titleMatcher = matcher.get();
                List<CellTitle> matched = titleMatcher.match(cellTitles);
                PropertyConverter converter = getPropertyConverter(property, titleMatcher.getOperation(), matched);
                converters.add(converter);
            }
        }
        int hit = Sets.filter(converters, PropertyConverter::isMatched).size();
        if (hit == 0) {
            return Collections.emptySet();
        }
        return converters;
    }

    private PropertyConverter getPropertyConverter(Property property, Operation operation, final List<CellTitle> titles) {
        final PropertyConverter converter;
        final JavaType javaType = property.getJavaType();
        Collection<ValueHandler> valueHandlers = Lists.newArrayList(readContentValueHandlers);
        property.getValueHandlers().forEach(valueHandler -> valueHandlers.add(valueHandler));
        if (operation == Operation.LINE_NUM) {
            converter = new LineNumberConverter(property, titles);
        } else if (javaType.isCollectionLikeType()) {
            converter = new CollectionConverter(property, titles, valueHandlers);
        } else if (javaType.isMapLikeType()) {
            if (javaType.getKeyType().getRawClass() == Integer.class) {
                converter = new MapIntKeyConverter(property, titles, valueHandlers);
            } else if (javaType.getKeyType().getRawClass() == String.class) {
                converter = new MapStringKeyConverter(property, titles, valueHandlers);
            } else if (javaType.getKeyType().getRawClass() == Sequence.class) {
                converter = new MapSequenceKeyConverter(property, titles, valueHandlers);
            } else {
                String format = String.format("Map类型的key只支持 %s(cell index) | %s (cell name ) | %s (cell sequence)", Integer.class, String.class, Sequence.class);
                throw new SettingException(format);
            }
        } else {
            if (titles.isEmpty()) {
                converter = new NoneConverter(property);
            } else if (titles.size() == 1) {
                converter = new BeanConverter(property, titles.get(0), valueHandlers);
            } else {
                log.error("-> property is [{}] ,type is [{}]", property.getName(), javaType);
                titles.forEach(title -> log.error("匹配到多个列", title));
                String format = String.format("property is %s ,type is %s , 匹配到多列", property.getName(), javaType);
                throw new SettingException(format);
            }
        }
        return converter;
    }

    private List<CellTitle> getCellTitles(Row row) {
        List<CellTitle> titles = new ArrayList<>(row.getLastCellNum());
        for (Cell cell : row) {
            String value = ValueUtil.getCellValue(cell, titleValueHandlers);
            CellTitle title = new CellTitle(row.getRowNum(), cell.getColumnIndex(), value);
            titles.add(title);
        }
        return titles;
    }

}