package com.github.fnwib.parse;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.convert.ExcelConversionService;
import com.github.fnwib.convert.ExcelConverter;
import com.github.fnwib.convert.ExcelGenericConversionService;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.read.ReadParser;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.WriteParser;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseImpl<T> implements Parser<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseImpl.class);

    private Map<PropertyDescriptor, Title> PROPERTY_DESCRIPTOR_MAP = Maps.newHashMap();

    private final double RATIO;

    private final Class<T> clazz;

    private final ExcelConversionService conversionService;

    public ParseImpl(Class<T> clazz,
                     ExcelGenericConversionService converterRegistry,
                     double ratio) {
        this.clazz = clazz;
        this.RATIO = ratio;
        this.conversionService = converterRegistry;
    }

    @Override
    public boolean match(Row row) {
        if (row == null) {
            return false;
        }
        createMapping(row);
        return !PROPERTY_DESCRIPTOR_MAP.isEmpty();
    }

    public void createMapping(Row row) {
        if (row == null)
            return;
        Map<String, Title> map = new HashMap<>();
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, CellType.class);
        for (Field field : fields) {
            field.setAccessible(true);
            CellType type = field.getAnnotation(CellType.class);
            Title title;
            if (type.type() == Operation.LINE_NUM) {
                title = new Title(field.getName(), type);
            } else if (conversionService.canConvert(field.getType())) {
                List<TitleDesc> list = this.getRule(row, type);
                title = new Title(field.getName(), type, list);
                if (!title.isSerial()) {
                    throw new NotSupportedException(title.getFieldName() + " matched index is not continuous");
                }
                ExcelConverter<?> convert = conversionService.findConvert(field.getType());
                title.setConverter(convert);
                LOGGER.debug("-->field name is '{}', field title is '{}', excel title is '{}' ", field.getName(), title, list);
            } else {
                throw new NotSupportedException("not support convert " + field.getType() + ",Please support this type by yourself");
            }
            map.put(field.getName(), title);
        }
        Map<String, Title> matched = Maps.filterValues(map, title -> title.getList().size() > 0);
        if ((double) matched.size() / map.size() < RATIO) {
            return;
        }
        try {
            PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz)
                    .getPropertyDescriptors();
            for (PropertyDescriptor descriptor : propertyDescriptors) {
                Title title = map.get(descriptor.getName());
                if (title != null) {
                    PROPERTY_DESCRIPTOR_MAP.put(descriptor, title);
                }
            }
        } catch (IntrospectionException e) {
            LOGGER.error("-> ", e);
            throw new ExcelException(e);
        }
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
    public ReadParser<T> createReadParser() {
        if (PROPERTY_DESCRIPTOR_MAP.isEmpty()) {
            throw new ExcelException("没有找到Excel表头与" + clazz.getName() + "的映射关系");
        }
        return new ReadParser<>(clazz, PROPERTY_DESCRIPTOR_MAP);
    }


    @Override
    public WriteParser<T> createWriteParser() {
        if (PROPERTY_DESCRIPTOR_MAP.isEmpty()) {
            throw new ExcelException("没有找到Excel表头与" + clazz.getName() + "的映射关系");
        }
        return new WriteParser<>(clazz, PROPERTY_DESCRIPTOR_MAP);
    }

}
