package com.github.fnwib.databing;

import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.title.TitleResolver;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.jackson.Json;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class LineReaderForExcel<T> implements LineReader<T> {

    private final Class<T>               entityClass;
    private final TitleResolver          titleResolver;
    private final Set<PropertyConverter> converters;

    public LineReaderForExcel(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.titleResolver = Context.INSTANCE.getContextConfig().getTitleResolver();
        this.converters = Sets.newHashSet();
    }

    public LineReaderForExcel(Class<T> entityClass, LocalConfig localConfig) {
        this.entityClass = entityClass;
        this.titleResolver = localConfig.getTitleResolver();
        this.converters = Sets.newHashSet();
    }

    @Override
    public boolean isEmpty(Row row) {
        if (row == null) {
            return true;
        }
        for (Cell cell : row) {
            if (cell != null && StringUtils.isNotBlank(cell.getStringCellValue())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean match(Row row) {
        if (isEmpty(row)) {
            return false;
        }
        if (!converters.isEmpty()) {
            converters.clear();
        }
        Set<PropertyConverter> titleTokens = titleResolver.resolve(entityClass, row);
        boolean flag = !titleTokens.isEmpty();
        if (flag) {
            titleTokens.forEach(t -> this.converters.add(t));
        }
        return flag;
    }

    @Override
    public Optional<T> convert(Row row) {
        Map<String, Object> map = convertToMap(row);
        if (map.isEmpty()) {
            return Optional.empty();
        }
        T t = Json.Mapper.convertValue(map, entityClass);
        return Optional.of(t);
    }

    @Override
    public Map<String, Object> convertToMap(Row row) {
        if (isEmpty(row)) {
            return Collections.emptyMap();
        }
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(converters.size());
        for (PropertyConverter converter : converters) {
            String mapKey = converter.getKey();
            Optional<?> mapValue = converter.getValue(row);
            if (mapValue.isPresent()) {
                result.put(mapKey, mapValue.get());
            }
        }
        return result;
    }

    @Override
    public LineWriter<T> getLineWriter() {
        if (converters.isEmpty()) {
            throw new SettingException("没有匹配到Title");
        }
        return new LineWriterForExcel<>(converters);
    }
}
