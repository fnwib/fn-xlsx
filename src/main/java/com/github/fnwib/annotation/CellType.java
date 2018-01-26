package com.github.fnwib.annotation;

import com.github.fnwib.databing.valuehandler.ValueHandler;

import java.lang.annotation.*;

@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.METHOD})
public @interface CellType {
    /**
     * title 前缀
     * 完整匹配-不支持正则
     *
     * @return
     */
    String prefix() default "";

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
     * 要排除的Excel列title
     * 支持正则表达式
     * <p>
     * title的子集
     *
     * @return
     */
    String exclude() default "";

    /**
     * title 后缀
     * 完整匹配-不支持正则
     *
     * @return
     */
    String suffix() default "";

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
     * 值处理器
     *
     * @return
     */
    Class<? extends ValueHandler<String>>[] handlers() default {};


}
