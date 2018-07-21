package com.github.fnwib.testentity;

import com.github.fnwib.plugin.ValueHandler;

public class ToUpperHandler implements ValueHandler {

    @Override
    public String convert(String param) {
        return param.toUpperCase();
    }
}
