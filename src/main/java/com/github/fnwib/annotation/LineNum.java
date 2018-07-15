package com.github.fnwib.annotation;

import java.lang.annotation.*;

/**
 * 读取Excel行号
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface LineNum {

}
