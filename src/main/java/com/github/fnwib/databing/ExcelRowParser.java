package com.github.fnwib.databing;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;

import java.util.Map;
import java.util.Set;

@Slf4j
public final class ExcelRowParser<T> implements RowParser<T> {
    private final static ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private final Class<T>           entityClass;
    private       Set<PropertyToken> titleTokens;

    public ExcelRowParser(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    public boolean match(Row row) {
        if (row == null) {
            return false;
        }
        if (titleTokens != null) {
            titleTokens.clear();
        }
        titleTokens = Context.INSTANCE.resolve(entityClass, row);
        return !titleTokens.isEmpty();
    }

    public T convert(Row row) {
        Map<String, Object> result = Maps.newHashMapWithExpectedSize(titleTokens.size());
        for (PropertyToken titleToken : titleTokens) {
            String mapKey = titleToken.getMapKey();
            Object mapValue = titleToken.getMapValue(row);
            result.put(mapKey, mapValue);
        }
        log.debug("source ->[{}] [{}]", row.getRowNum(), result);
        return MAPPER.convertValue(result, entityClass);
    }


}
