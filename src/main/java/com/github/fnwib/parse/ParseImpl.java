package com.github.fnwib.parse;

import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.convert.ExcelConversionService;
import com.github.fnwib.convert.ExcelConverter;
import com.github.fnwib.convert.ExcelGenericConversionService;
import com.github.fnwib.exception.ExcelException;
import com.github.fnwib.exception.NotSupportedException;
import com.github.fnwib.read.ReadParser;
import com.github.fnwib.reflect.BeanResolver;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.util.ValueUtil;
import com.github.fnwib.write.WriteParser;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ParseImpl<T> implements Parser<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ParseImpl.class);

    private Map<Property, Title> PROPERTY_MAP = Maps.newHashMap();

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
        if (!PROPERTY_MAP.isEmpty()) {
            PROPERTY_MAP.clear();
        }
        createMapping(row);
        return !PROPERTY_MAP.isEmpty();
    }

    private void createMapping(Row row) {
        if (row == null)
            return;
        Map<Property, Title> map = new HashMap<>();
        List<Property> properties = BeanResolver.INSTANCE.getPropertiesWithAnnotation(clazz, CellType.class);
        for (Property property : properties) {
            Field field = property.getField();
            CellType type = field.getAnnotation(CellType.class);
            if (type.operation() == Operation.LINE_NUM) {
                map.put(property, new Title(field.getName(), type));
            } else if (conversionService.canConvert(property.getJavaType())) {
                List<TitleDesc> list = this.getRule(row, type);
                Title title;
                if (list.isEmpty()) {
                    title = new Title();
                } else {
                    title = new Title(field.getName(), type, list);
                    if (!title.isSerial()) {
                        throw new NotSupportedException(title.getFieldName() + " matched index is not continuous");
                    }
                }
                ExcelConverter<?> convert = conversionService.findConvert(property.getJavaType());
                title.setConverter(convert);
                map.put(property, title);
                LOGGER.debug("-->field name is '{}',  excel title is '{}' ", field.getName(), list);
            } else {
                throw new NotSupportedException("not support convert " + property.getJavaType() + ",Please support this type by yourself");
            }

        }
        int hit = Maps.filterValues(map, v -> !v.isEmpty()).size();
        if ((double) hit / properties.size() < RATIO) {
            return;
        }
        map.forEach((property, title) -> PROPERTY_MAP.put(property, title));
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
        if (PROPERTY_MAP.isEmpty()) {
            throw new ExcelException("没有找到Excel表头与" + clazz.getName() + "的映射关系");
        }
        return new ReadParser<>(clazz, PROPERTY_MAP);
    }

    @Override
    public WriteParser<T> createWriteParser() {
        if (PROPERTY_MAP.isEmpty()) {
            throw new ExcelException("没有找到Excel表头与" + clazz.getName() + "的映射关系");
        }
        return new WriteParser<>(Maps.filterValues(PROPERTY_MAP, v -> !v.isEmpty()));
    }

}
