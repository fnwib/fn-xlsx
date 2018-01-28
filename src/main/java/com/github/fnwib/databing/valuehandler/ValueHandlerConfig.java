package com.github.fnwib.databing.valuehandler;

import com.google.common.collect.Lists;

import java.util.List;

public class ValueHandlerConfig {

    private List<ValueHandler> valueHandlers = Lists.newArrayList();

    public void register(ValueHandler valueHandler) {
        valueHandlers.add(valueHandler);
    }

    public List<ValueHandler> getValueHandlers() {
        List<ValueHandler> valueHandlers = Lists.newArrayList();
        for (ValueHandler valueHandler : this.valueHandlers) {
            valueHandlers.add(valueHandler);
        }
        return valueHandlers;
    }
}