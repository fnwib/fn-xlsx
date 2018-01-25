package model;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.util.ValueUtil;

public class RecordHandler implements ValueHandler<String> {
    @Override
    public String convert(String param) {
        return ValueUtil.sortAndTrim(param, "/");
    }
}
