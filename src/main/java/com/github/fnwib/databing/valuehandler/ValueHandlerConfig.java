package com.github.fnwib.databing.valuehandler;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;

import java.util.Collection;

public class ValueHandlerConfig {

    private Collection<ValueHandler> valueHandlers = Queues.newLinkedBlockingDeque();

    public synchronized void register(ValueHandler valueHandler) {
        if (valueHandlers.contains(valueHandler)) {
            return;
        }
        valueHandlers.add(valueHandler);
    }

    public Collection<ValueHandler> getValueHandlers() {
        Collection<ValueHandler> valueHandlers = Lists.newArrayList();
        for (ValueHandler valueHandler : this.valueHandlers) {
            valueHandlers.add(valueHandler);
        }
        return valueHandlers;
    }
}