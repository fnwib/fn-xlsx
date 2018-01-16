package com.github.fnwib.annotation;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AutoParse {
    /**
     * Excel的列title 名称
     * 支持正则表达式
     * <p>
     * "aaa 12"  -> "aaa \\d"
     * "aaa 汉字"  -> "aaa.*"
     * "aaa" "bbb"  -> "aaa|bbb"
     * <p>
     */
    String title() default "";

    /**
     * 对Excel列的操作类型
     * <p>
     * LINE_NUM 字段是excel行号 从1开始
     * DEFAULT 不变
     * REORDER
     * <p>
     *
     * @return
     */
    Operation operation() default Operation.DEFAULT;

    /**
     * 要排除的Excel列title
     * 支持正则表达式
     *
     * @return
     */
    String exclude() default "";


}
