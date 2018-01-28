package model;

import com.github.fnwib.databing.valuehandler.ValueHandler;

public class ToUpperHandler implements ValueHandler {

    @Override
    public String convert(String param) {
        return param.toUpperCase();
    }
}
