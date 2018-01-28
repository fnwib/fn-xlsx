package com.github.fnwib.databing.title;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.annotation.ReadValueHandler;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.convert.impl.*;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.util.ValueUtil;
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
import java.util.stream.Collectors;

/**
 * title解析器  将Excel.Row与Entity.Field做映射
 */
@Slf4j
public class TitleResolver {

    private final List<ValueHandler<String>> titleValueHandlers = Lists.newArrayList();

    /**
     * 注册title处理器
     *
     * @param valueHandlers
     */
    public void register(List<ValueHandler<String>> valueHandlers) {
        for (ValueHandler valueHandler : valueHandlers) {
            this.titleValueHandlers.add(valueHandler);
        }
    }

    /**
     * 注册title处理器
     *
     * @param valueHandlers
     */
    public void register(ValueHandler<String>... valueHandlers) {
        for (ValueHandler<String> valueHandler : valueHandlers) {
            this.titleValueHandlers.add(valueHandler);
        }
    }

    public Set<PropertyConverter> resolve(Class<?> entityClass, Row row) {
        List<CellTitle> cellTitles = getCellTitles(row);
        List<Property> properties = BeanResolver.INSTANCE.getProperties(entityClass);
        Set<PropertyConverter> converters = Sets.newHashSet();
        for (Property property : properties) {
            final CellType cellType = property.getAnnotation(CellType.class);
            final AutoMapping mapping = property.getAnnotation(AutoMapping.class);
            final ReadValueHandler handler = property.getAnnotation(ReadValueHandler.class);
            if (mapping != null) {
                PropertyConverter converter = getPropertyConverter(property, cellTitles, mapping, handler);
                converters.add(converter);
                continue;
            }
            if (cellType != null) {
                PropertyConverter converter = getPropertyConverter(property, cellTitles, cellType);
                converters.add(converter);
                continue;
            }
        }
        int hit = Sets.filter(converters, p -> p.isMatched()).size();
        if (hit == 0) {
            return Collections.emptySet();
        }
        return converters;
    }

    private PropertyConverter getPropertyConverter(Property property, List<CellTitle> cellTitles, CellType cellType) {
        TitleMatcher titleMatcher = new TitleMatcher(cellType);
        List<CellTitle> match = titleMatcher.match(cellTitles);
        if (cellType.operation() == Operation.LINE_NUM) {
            final CellTitle title;
            if (StringUtils.isBlank(cellType.title())) {
                title = null;
            } else {
                title = match.get(0);
            }
            return new LineNumberConverter(property, title);
        } else if (cellType.operation() == Operation.REORDER) {
            throw new SettingException("不支持类型,请使用@AutoMapping 的handler自行实现值处理");
        }
        return getPropertyConverter(property, match, Collections.emptyList());
    }


    private PropertyConverter getPropertyConverter(Property property, List<CellTitle> cellTitles, AutoMapping
            mapping, ReadValueHandler handler) {
        Objects.requireNonNull(mapping);
        TitleMatcher titleMatcher = new TitleMatcher(mapping);
        List<CellTitle> match = titleMatcher.match(cellTitles);
        if (mapping.operation() == Operation.LINE_NUM) {
            final CellTitle title;
            if (StringUtils.isAnyBlank(mapping.prefix(), mapping.value(), mapping.suffix())) {
                title = null;
            } else {
                title = match.get(0);
            }
            return new LineNumberConverter(property, title);
        } else if (mapping.operation() == Operation.REORDER) {
            throw new SettingException("不支持类型,请使用@AutoMapping 的handler自行实现值处理");
        }

        if (match.size() > 1) {
            List<TitleValidator> titleValidators = convertTitleValidate(mapping.validate());
            List<CellTitle> titles = deepCopy(match);
            for (TitleValidator titleValidator : titleValidators) {
                boolean validate = titleValidator.validate(titles);
                if (!validate) throw new SettingException("Excel Title 顺序错误");
            }
        }
        List<ValueHandler<String>> valueHandlers = getValueHandlers(handler);
        return getPropertyConverter(property, match, valueHandlers);
    }

    private PropertyConverter getPropertyConverter(Property property, List<CellTitle> titles, List<ValueHandler<String>> valueHandlers) {
        final PropertyConverter converter;
        final JavaType javaType = property.getJavaType();
        if (javaType.isCollectionLikeType()) {
            converter = new CollectionConverter(property, titles, valueHandlers);
        } else if (javaType.isMapLikeType()) {
            if (javaType.getKeyType().getRawClass() == Integer.class) {
                converter = new MapIntKeyConverter(property, titles, valueHandlers);
            } else if (javaType.getKeyType().getRawClass() == String.class) {
                Map<String, List<CellTitle>> titleNames = titles.stream().collect(Collectors.groupingBy(CellTitle::getText));
                titleNames = Maps.filterValues(titleNames, v -> v.size() > 1);
                if (titleNames.size() > 0) {
                    log.error("-> property is [{}] ,type is [{}]", property.getName(), javaType);
                    titleNames.forEach((titleName, sameNameTitles) -> log.error("-> 存在相同名称的tile[{}]", sameNameTitles));
                    throw new SettingException("Map类型的key是String(title name)匹配到title存在相同的名称");
                }
                converter = new MapStringKeyConverter(property, titles, valueHandlers);
            } else {
                throw new SettingException("Map类型的key只支持Integer(cell index)或String(title name)");
            }
        } else {
            if (titles.isEmpty()) {
                converter = new SingleConverter(property, null, valueHandlers);
            } else {
                converter = new SingleConverter(property, titles.get(0), valueHandlers);
            }
        }
        return converter;
    }


    private List<ValueHandler<String>> getValueHandlers(ReadValueHandler handler) {
        List<ValueHandler<String>> valueHandlers = Context.INSTANCE.findContentValueHandlers();
        if (handler != null) {
            for (Class<? extends ValueHandler<String>> h : handler.value()) {
                Constructor<?>[] constructors = h.getConstructors();
                if (constructors.length == 1) {
                    Constructor<?> constructor = constructors[0];
                    try {
                        ValueHandler<String> valueHandler = (ValueHandler<String>) constructor.newInstance();
                        valueHandlers.add(valueHandler);
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        throw new SettingException(h.getName() + " no found non args constructor");
                    }
                } else {
                    throw new SettingException(h.getName() + " not support multi args constructor");
                }
            }
        }
        return valueHandlers;
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

    private List<TitleValidator> convertTitleValidate(Class<? extends TitleValidator>[] validates) {
        if (validates.length == 0) return Collections.emptyList();
        List<TitleValidator> validateList = Lists.newArrayListWithCapacity(validates.length);
        for (Class<? extends TitleValidator> validatorClass : validates) {
            Constructor<?>[] constructors = validatorClass.getConstructors();
            if (constructors.length == 1) {
                Constructor<?> constructor = constructors[0];
                try {
                    TitleValidator titleValidator = (TitleValidator) constructor.newInstance();
                    validateList.add(titleValidator);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new SettingException(validatorClass.getName() + " no found non args constructor");
                }
            } else {
                throw new SettingException(validatorClass.getName() + " not support multi args constructor");
            }
        }
        return validateList;
    }

    public List<CellTitle> deepCopy(List<CellTitle> cellTitles) {
        if (cellTitles.isEmpty()) return cellTitles;
        List<CellTitle> result = Lists.newArrayListWithCapacity(cellTitles.size());
        cellTitles.forEach(t -> result.add(t.clone()));
        return result;
    }

}