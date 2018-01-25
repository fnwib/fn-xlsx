package model;

import com.github.fnwib.databing.valuehandler.ValueHandler;

public class ToUpperHandler implements ValueHandler<String> {

    @Override
    public String convert(String param) {
        return param.toUpperCase();
    }
}