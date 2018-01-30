package com.github.fnwib.databing.convert.impl;

import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.CellText;
import org.apache.poi.ss.usermodel.Row;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class NoneConverter implements PropertyConverter {

    private final Property property;

    public NoneConverter(Property property) {
        this.property = property;
    }

    @Override
    public boolean isMatched() {
        return false;
    }

    @Override
    public String getKey() {
        return property.getName();
    }

    @Override
    public Optional<String> getValue(Row row) {
        return Optional.empty();
    }

    @Override
    public <Param> List<CellText> getCellText(Param element) {
        return Collections.emptyList();
    }

}
