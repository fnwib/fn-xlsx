package com.github.fnwib.read;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.convert.ExcelConverter;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.PropertyException;
import com.github.fnwib.parse.Title;
import com.github.fnwib.util.ValueUtil;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class ReadParser<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReadParser.class);

    private Class<T> entityClass;

    private final Map<Method, Title> RULES = new HashMap<>();

    public ReadParser(Class<T> entityClass, Map<PropertyDescriptor, Title> rules) {
        this.entityClass = entityClass;
        try {
            initRules(rules);
        } catch (IntrospectionException e) {
            throw new ExcelException(e);
        }
    }


    private void initRules(Map<PropertyDescriptor, Title> rules) throws IntrospectionException {
        rules.forEach((propertyDescriptor, title) -> {
            if (propertyDescriptor.getWriteMethod() == null) {
                throw new PropertyException(propertyDescriptor.getName() + "没有标准的setter");
            } else {
                LOGGER.debug("property is '{}' , setter is '{}'", propertyDescriptor.getName(), propertyDescriptor.getWriteMethod().getName());
                RULES.put(propertyDescriptor.getWriteMethod(), title);
            }
        });
    }

    public T convert(Row row) throws ExcelException {
        try {
            Constructor<T> constructor = entityClass.getConstructor();
            T t = constructor.newInstance();
            RULES.forEach((method, title) -> {
                try {
                    CellType cellType = title.getCellType();
                    if (cellType.type() == Operation.LINE_NUM) {
                        Object value = row.getRowNum() + 1;
                        method.invoke(t, value);
                        return;
                    }
                    ExcelConverter<?> converter = title.getConverter();
                    Object value = converter.convert(title, row);
                    if (cellType.type() == Operation.REORDER) {
                        value = ValueUtil.sortAndTrim(value.toString(), "/");
                    }
                    method.invoke(t, value);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            });
            return t;
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
        }
    }

}
