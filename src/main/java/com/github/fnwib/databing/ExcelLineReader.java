package com.github.fnwib.databing;

import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.title.TitleResolver;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.jackson.Json;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Set;

public class ExcelLineReader<T> implements LineReader<T> {
    private static final Logger log = LoggerFactory.getLogger(ExcelLineReader.class);

    private final Class<T>               entityClass;
    private final TitleResolver          titleResolver;
    private final Set<PropertyConverter> converters;

    public ExcelLineReader(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.titleResolver = Context.INSTANCE.getContextConfig().getTitleResolver();
        this.converters = Sets.newHashSet();
    }

    public ExcelLineReader(Class<T> entityClass, LocalConfig localConfig) {
        this.entityClass = entityClass;
        this.titleResolver = localConfig.getTitleResolver();
        this.converters = Sets.newHashSet();
    }

    @Override
    public boolean match(Row row) {
        if (row == null) {
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
    public T read(Row row) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(converters.size());
        for (PropertyConverter converter : converters) {
            String mapKey = converter.getKey();
            Object mapValue = converter.getValue(row);
            result.put(mapKey, mapValue);
        }
        log.debug("source ->[{}] [{}]", row.getRowNum(), result);
        return Json.Mapper.convertValue(result, entityClass);
    }

    @Override
    public LineWriter<T> getLineWriter() {
        if (converters.isEmpty()) {
            throw new SettingException("没有匹配到Title");
        }
        return new ExcelLineWriter<>(converters);
    }
}
