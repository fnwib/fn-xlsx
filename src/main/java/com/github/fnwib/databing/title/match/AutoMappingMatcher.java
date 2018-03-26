package com.github.fnwib.databing.title.match;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.title.CellTitle;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.regex.Pattern;

public class AutoMappingMatcher implements TitleMatcher {

    private static final Logger log = LoggerFactory.getLogger(TitleMatcher.class);
    private final Pattern titlePattern;

    private final Operation operation;
    private final String    sequence;
    private final String    exclude;

    public AutoMappingMatcher(AutoMapping mapping) {
        this.titlePattern = Pattern.compile(mapping.value().trim(), Pattern.CASE_INSENSITIVE);
        this.operation = mapping.operation();
        this.sequence = mapping.value().trim();
        this.exclude = mapping.exclude();
    }

    @Override
    public List<CellTitle> match(List<CellTitle> titles) {

        List<CellTitle> result = Lists.newArrayList();

        return result;
    }

    @Override
    public Operation getOperation() {
        return operation;
    }

}
