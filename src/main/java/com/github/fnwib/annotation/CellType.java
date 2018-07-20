package com.github.fnwib.annotation;

import java.lang.annotation.*;

/**
 * com.github.fnwib.annotation.AutoMapping
 */
@Deprecated
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface CellType {

	/**
	 * <p>
	 * LINE_NUM 字段是excel行号 从1开始
	 * DEFAULT Value
	 * <p>
	 *
	 * @return
	 */
	Operation operation() default Operation.DEFAULT;


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
	 * "aaa 12"  -> "aaa \\d+"
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
	 * 同一层级绑定优先级
	 *
	 * @return
	 */
	int order() default Ordered.LOWEST_PRECEDENCE;

	/**
	 * 是否独占绑定结果
	 *
	 * @return 默认共享
	 */
	BindType bindType() default BindType.Nonexclusive;


	/**
	 * Nested 嵌套类型(最多两层)
	 * Flat  非嵌套类型
	 *
	 * @return
	 */
	ComplexEnum complex() default ComplexEnum.Flat;

	/**
	 * true 属性不参与序列化
	 * false 属性参与序列化
	 *
	 * @return 是否参与序列化的配置
	 */
	boolean readonly() default false;

}
