package com.github.fnwib.databing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum Json {

    Mapper;

    private final ObjectMapper mapper;

    Json() {
        this.mapper = new ObjectMapper().findAndRegisterModules();
    }

    public ObjectMapper get() {
        return mapper;
    }

    public String writeValueAsString(Object value) {
        try {
            return mapper.writeValueAsString(value);
        } catch (JsonProcessingException e) {
            log.error("--> error", e);
            throw new RuntimeException(e);
        }
    }

    public <T> T convertValue(Object fromValue, Class<T> toValueType)
            throws IllegalArgumentException {
        return mapper.convertValue(fromValue, toValueType);
    }


}
