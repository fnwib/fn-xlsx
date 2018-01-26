package com.github.fnwib.databing;

import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.title.TitleValidator;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.reflect.Property;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;

@Slf4j
public final class PropertyToken {

    private ReadToken       readToken;
    private WriteToken      writeToken;
    private List<CellTitle> titles;

    public PropertyToken(Property property, Operation operation) {
        this.readToken = new ReadToken(property, operation);
        this.writeToken = new WriteToken(property);
        this.titles = Collections.emptyList();
    }

    public PropertyToken(Property property,
                         Operation operation,
                         List<CellTitle> titles,
                         List<ValueHandler<String>> valueHandlers) {
        this.readToken = new ReadToken(property, operation, titles, valueHandlers);
        this.writeToken = new WriteToken(property, titles);
        this.titles = titles;
    }

    public boolean isMatchEmpty() {
        return titles.isEmpty();
    }

    public ReadToken getReadToken() {
        return readToken;
    }

    public WriteToken getWriteToken() {
        return writeToken;
    }
}
