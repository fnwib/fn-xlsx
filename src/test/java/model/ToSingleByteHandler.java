package model;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.util.BCConvert;

public class ToSingleByteHandler implements ValueHandler {
    @Override
    public String convert(String param) {
        return BCConvert.toSingleByte(param);
    }
}
