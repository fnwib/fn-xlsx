package com.github.fnwib.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.github.fnwib.databing.title.Sequence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public enum Json {

    Mapper;

    private static final Logger log = LoggerFactory.getLogger(Json.class);
    private final ObjectMapper mapper;

    Json() {
        this.mapper = new ObjectMapper().findAndRegisterModules();
        SimpleModule m = new SimpleModule();
        m.addKeyDeserializer(Sequence.class, new CellSequenceDeserializer());
        mapper.registerModule(m);
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