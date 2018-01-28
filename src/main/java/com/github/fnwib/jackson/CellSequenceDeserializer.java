package com.github.fnwib.jackson;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.github.fnwib.databing.title.Sequence;

import java.io.IOException;

public class CellSequenceDeserializer extends KeyDeserializer {

    @Override
    public Sequence deserializeKey(String key, DeserializationContext ctxt) throws IOException {
        if (key == null) {
            return null;
        }
        try {
            return new Sequence(key);
        } catch (Exception re) {
            throw ctxt.weirdStringException(key, Sequence.class, "");
        }
    }


}
