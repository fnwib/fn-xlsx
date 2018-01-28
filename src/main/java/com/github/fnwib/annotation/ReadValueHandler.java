package com.github.fnwib.annotation;

import com.github.fnwib.databing.valuehandler.ValueHandler;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ReadValueHandler {

    /**
     * 值处理器
     *
     * @return
     */
    Class<? extends ValueHandler>[] value() default {};
}

