package com.github.fnwib.jackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.github.fnwib.databing.title.CellTitle;

import java.io.IOException;

public class CellTitleKeyDeserializer extends KeyDeserializer {

    @Override
    public CellTitle deserializeKey(String key, DeserializationContext ctxt) throws IOException{
        if (key == null) {
            return null;
        }
        try {
            return Json.Mapper.get().readValue(key, CellTitle.class);
        } catch (Exception re) {
            throw ctxt.weirdStringException(key, CellTitle.class, "");
        }
    }


}
