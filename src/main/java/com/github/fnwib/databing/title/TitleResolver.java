package com.github.fnwib.databing.title;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.PropertyToken;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.title.TitleMatcher;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.util.ValueUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * title解析器  将Excel.Row与Entity.Field做映射
 */
@Slf4j
public class TitleResolver {

    private final List<ValueHandler<String>> valueHandlers = Lists.newArrayList();

    /**
     * 注册title处理器
     *
     * @param valueHandlers
     */
    public void register(List<ValueHandler<String>> valueHandlers) {
        for (ValueHandler valueHandler : valueHandlers) {
            this.valueHandlers.add(valueHandler);
        }
    }

    /**
     * 注册title处理器
     *
     * @param valueHandlers
     */
    public void register(ValueHandler<String>... valueHandlers) {
        for (ValueHandler<String> valueHandler : valueHandlers) {
            this.valueHandlers.add(valueHandler);
        }
    }

    public Set<PropertyToken> resolve(Class<?> entityClass, Row row) {
        List<CellTitle> cellTitles = getCellTitles(row);
        List<Property> properties = BeanResolver.INSTANCE.getPropertiesWithAnnotation(entityClass, AutoMapping.class);
        Set<PropertyToken> titleTokens = Sets.newHashSet();
        for (Property property : properties) {
            final AutoMapping mapping = property.getAnnotation(AutoMapping.class);
            final PropertyToken token;
            if (mapping.operation() == Operation.LINE_NUM) {
                token = new PropertyToken(property);
            } else {
                TitleMatcher titleMatcher = new TitleMatcher(mapping);
                List<CellTitle> match = titleMatcher.match(cellTitles);

                List<ValueHandler<String>> contentValueHandlers = Context.INSTANCE.findContentValueHandlers();
                for (ValueHandler<String> handler : convert(mapping.handlers())) {
                    contentValueHandlers.add(handler);
                }
                token = new PropertyToken(property, match, contentValueHandlers);
            }
            titleTokens.add(token);
        }
        int hit = Sets.filter(titleTokens, p -> !p.isMatchEmpty()).size();
        if (hit == 0) {
            return Collections.emptySet();
        }
        return titleTokens;
    }


    private List<CellTitle> getCellTitles(Row row) {
        List<CellTitle> titles = new ArrayList<>(row.getLastCellNum());
        for (Cell cell : row) {
            String value = ValueUtil.getCellValue(cell, valueHandlers);
            CellTitle title = new CellTitle(row.getRowNum(), cell.getColumnIndex(), value);
            titles.add(title);
        }
        return titles;
    }

    private List<ValueHandler<String>> convert(Class<? extends ValueHandler<String>>[] handlers) {
        List<ValueHandler<String>> handlerList = Lists.newArrayListWithCapacity(handlers.length);
        for (Class<? extends ValueHandler<String>> handler : handlers) {
            Constructor<?>[] constructors = handler.getConstructors();
            if (constructors.length == 1) {
                Constructor<?> constructor = constructors[0];
                try {
                    ValueHandler<String> valueHandler = (ValueHandler<String>) constructor.newInstance();
                    handlerList.add(valueHandler);
                } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                    throw new SettingException(handler.getName() + " no found non args constructor");
                }
            } else {
                throw new SettingException(handler.getName() + " not support multi args constructor");
            }
        }
        return handlerList;
    }


}