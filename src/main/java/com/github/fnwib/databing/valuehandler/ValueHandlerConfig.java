package com.github.fnwib.databing.valuehandler;

import com.google.common.collect.Lists;

import java.util.List;

public class ValueHandlerConfig {

    private List<ValueHandler<String>> valueHandlers = Lists.newArrayList();

    public void register(ValueHandler<String> valueHandler) {
        valueHandlers.add(valueHandler);
    }

    public List<ValueHandler<String>> getValueHandlers() {
        List<ValueHandler<String>> valueHandlers = Lists.newArrayList();
        for (ValueHandler<String> valueHandler : this.valueHandlers) {
            valueHandlers.add(valueHandler);
        }
        return valueHandlers;
    }
}