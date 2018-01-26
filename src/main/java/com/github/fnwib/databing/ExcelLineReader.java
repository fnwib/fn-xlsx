package com.github.fnwib.databing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.read.ReadParser;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;
import java.util.Set;

@Slf4j
public class ExcelLineReader<T> implements LineReader<T> {
    private final static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private final Class<T>           entityClass;
    private final Set<PropertyToken> titleTokens;

    public ExcelLineReader(Class<T> entityClass) {
        this.entityClass = entityClass;
        this.titleTokens = Sets.newHashSet();
    }

    @Override
    public boolean match(Row row) {
        if (row == null) {
            return false;
        }
        if (!titleTokens.isEmpty()) {
            titleTokens.clear();
        }
        Set<PropertyToken> titleTokens = Context.INSTANCE.resolve(entityClass, row);
        boolean flag = !titleTokens.isEmpty();
        if (flag) {
            titleTokens.forEach(t -> this.titleTokens.add(t));
        }
        return flag;
    }

    @Override
    public T read(Row row) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(titleTokens.size());
        for (PropertyToken titleToken : titleTokens) {
            ReadToken readToken = titleToken.getReadToken();
            String mapKey = readToken.getMapKey();
            Object mapValue = readToken.getMapValue(row);
            result.put(mapKey, mapValue);
        }
        log.debug("source ->[{}] [{}]", row.getRowNum(), result);
        return MAPPER.convertValue(result, entityClass);
    }

    @Override
    public LineWriter<T> getLineWriter() {
        if (titleTokens.isEmpty()) {
            throw new SettingException("没有匹配到Title");
        }
        return new ExcelLineWriter<>(titleTokens);
    }
}
