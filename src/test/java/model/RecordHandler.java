package model;

import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.google.common.base.Joiner;

import java.text.Collator;
import java.util.Locale;
import java.util.Set;
import java.util.TreeSet;

public class RecordHandler implements ValueHandler {

    private static final String symbols = "/";

    private static final Collator COLLATOR = Collator.getInstance(Locale.CHINA);

    @Override
    public String convert(String param) {

        if (param == null) {
            return null;
        }
        if (param.contains(symbols)) {
            String[] ps = param.split(symbols);
            Set<String> set = new TreeSet<>(COLLATOR);
            for (String p : ps) {
                set.add(p.trim());
            }
            return Joiner.on(symbols).join(set);
        } else {
            return param;
        }

    }

}
