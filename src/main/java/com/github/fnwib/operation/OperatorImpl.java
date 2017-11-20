package com.github.fnwib.operation;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.convert.ExcelConversionService;
import com.github.fnwib.convert.ExcelConverter;
import com.github.fnwib.convert.ExcelGenericConversionService;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.util.ValueUtil;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class OperatorImpl<T> implements Operator<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(OperatorImpl.class);

    private Map<Method, Title> RULES = new HashMap<>();

    private static final Pattern SEQUENCE_PATTERN = Pattern.compile("\\d+");

    private final double RATIO;

    private final Class<T> clazz;

    private final ExcelConversionService conversionService;

    public OperatorImpl(Class<T> clazz,
                        ExcelGenericConversionService converterRegistry,
                        double ratio) {
        this.clazz = clazz;
        this.RATIO = ratio;
        this.conversionService = converterRegistry;
    }

    @Override
    public boolean match(Row row) {
        if (row == null)
            return false;
        Map<String, List<TitleDesc>> map = new HashMap<>();
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, CellType.class);
        for (Field field : fields) {
            field.setAccessible(true);
            CellType type = field.getAnnotation(CellType.class);
            if (type == null) continue;
            String title = type.title();
            if (title.equals("")) {
                continue;
            }
            List<TitleDesc> list = this.getRule(row, type);
            LOGGER.debug("-->field name is '{}', field title is '{}', excel title is '{}' ", field.getName(), title, list);
            map.put(title, list);
        }
        Map<String, List<TitleDesc>> matched = Maps.filterValues(map, l -> l.size() > 0);
        if ((double) matched.size() / map.size() < RATIO) {
            return false;
        }

        for (Field field : fields) {
            field.setAccessible(true);
            CellType type = field.getAnnotation(CellType.class);
            try {
                Method method = clazz.getMethod("set" + StringUtils.capitalize(field.getName()), field.getType());

                Class parameterType = method.getParameterTypes()[0];
                if (type.type() == Operation.LINE_NUM) {
                    Title title = new Title(field.getName(), type);
                    RULES.put(method, title);
                } else if (conversionService.canConvert(parameterType)) {
                    List<TitleDesc> list = map.get(type.title());
                    Title title = new Title(field.getName(), type, list);
                    if (!title.isSerial()) {
                        throw new NotSupportedException(title.getFieldName() + " matched index is not continuous");
                    }
                    ExcelConverter<?> convert = conversionService.findConvert(parameterType);
                    title.setConverter(convert);
                    RULES.put(method, title);
                } else {
                    throw new NotSupportedException("not support convert " + parameterType.getName() + ",Please support this type by yourself");
                }
            } catch (NoSuchMethodException e) {
                throw new NotSupportedException("not such method : set" + StringUtils.capitalize(field.getName()));
            }

        }

        return true;
    }

    private List<TitleDesc> getRule(Row row, CellType type) {

        String title = type.title();
        String exclude = type.exclude();
        List<TitleDesc> list = new ArrayList<>();
        Pattern titlePattern = Pattern.compile(title);
        for (Cell cell : row) {
            String value = ValueUtil.getValue(cell, true, false);
            Matcher titleMatcher = titlePattern.matcher(value);
            if (titleMatcher.matches()) {
                if (StringUtils.isNotBlank(exclude) && Pattern.matches(exclude, value)) {
                    continue;
                }
                list.add(new TitleDesc(cell.getStringCellValue(), cell.getColumnIndex()));
            }
        }
        return list;
    }

    @Override
    public T convert(Row row) throws ExcelException {
        try {
            Constructor<T> constructor = clazz.getConstructor();
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
                } catch (ExcelException e) {
                    throw e;
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
