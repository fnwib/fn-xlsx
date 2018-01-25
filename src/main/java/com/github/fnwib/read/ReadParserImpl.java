package com.github.fnwib.read;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.convert.ExcelConverter;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.PropertyException;
import com.github.fnwib.parse.Title;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.util.ValueUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class ReadParserImpl<T> implements ReadParser<T> {

    private Class<T> entityClass;

    private final Map<Method, Title> RULES = new HashMap<>();

    public ReadParserImpl(Class<T> entityClass, Map<Property, Title> rules) {
        this.entityClass = entityClass;
        initRules(rules);
    }


    private void initRules(Map<Property, Title> rules) {
        rules.forEach((property, title) -> {
            PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
            if (propertyDescriptor.getWriteMethod() == null) {
                throw new PropertyException(propertyDescriptor.getName() + "没有标准的setter");
            } else {
                log.debug("property is '{}' , setter is '{}'", property.getName(), propertyDescriptor.getWriteMethod().getName());
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

                    if (title.isEmpty()) {
                        ExcelConverter<?> converter = title.getConverter();
                        method.invoke(t, converter.getDefaultValue());
                    } else {
                        CellType cellType = title.getCellType();
                        if (cellType.operation() == Operation.LINE_NUM) {
                            Object value = row.getRowNum() + 1;
                            method.invoke(t, value);
                            return;
                        }
                        ExcelConverter<?> converter = title.getConverter();
                        Object value = converter.convert(title, row);
                        if (cellType.operation() == Operation.REORDER) {
                            value = ValueUtil.sortAndTrim(value.toString(), "/");
                        }
                        method.invoke(t, value);
                    }
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
