package com.github.fnwib.databing.title;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
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
        List<Property> properties = BeanResolver.INSTANCE.getProperties(entityClass);
        Set<PropertyToken> titleTokens = Sets.newHashSet();
        for (Property property : properties) {
            final CellType cellType = property.getAnnotation(CellType.class);
            final AutoMapping mapping = property.getAnnotation(AutoMapping.class);
            if (cellType == null && mapping == null) {
                continue;
            } else {
                checkSupport(property.getJavaType());
                if (mapping != null) {
                    final PropertyToken token;
                    if (mapping.operation() == Operation.LINE_NUM) {
                        token = new PropertyToken(property, mapping.operation());
                    } else if (mapping.operation() == Operation.REORDER) {
                        throw new SettingException("不支持类型,请使用注解的ValueHandler自行实现值处理");
                    } else {
                        TitleMatcher titleMatcher = new TitleMatcher(mapping);
                        List<CellTitle> match = titleMatcher.match(cellTitles);

                        List<ValueHandler<String>> valueHandlers = Context.INSTANCE.findContentValueHandlers();
                        for (ValueHandler<String> handler : convertValueHandler(mapping.handlers())) {
                            valueHandlers.add(handler);
                        }
                        List<TitleValidator> titleValidators = convertTitleValidate(mapping.validate());
                        for (TitleValidator titleValidator : titleValidators) {
                            boolean validate = titleValidator.validate(match);
                            if (!validate) throw new SettingException("");
                        }
                        token = new PropertyToken(property, mapping.operation(), match, valueHandlers);
                    }
                    titleTokens.add(token);
                } else {
                    final PropertyToken token;
                    if (cellType.operation() == Operation.LINE_NUM) {
                        token = new PropertyToken(property, cellType.operation());
                    } else if (cellType.operation() == Operation.REORDER) {
                        throw new SettingException("不支持类型,请使用@AutoMapping 的handler自行实现值处理");
                    } else {
                        TitleMatcher titleMatcher = new TitleMatcher(cellType);
                        List<CellTitle> match = titleMatcher.match(cellTitles);
                        token = new PropertyToken(property, cellType.operation(), match, Collections.emptyList());
                    }
                    titleTokens.add(token);
                }
            }
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

    private List<ValueHandler<String>> convertValueHandler(Class<? extends ValueHandler<String>>[] handlers) {
        if (handlers.length == 0) return Collections.emptyList();
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


    private void checkSupport(JavaType javaType) {
        if (javaType.isMapLikeType()) {
            if (javaType.getKeyType().getRawClass() != java.lang.Integer.class) {
                throw new SettingException("Map类型的key只支持java.lang.Integer,值是cell的序号");
            }
        }
    }


}