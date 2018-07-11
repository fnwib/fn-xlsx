package com.github.fnwib.annotation;

import java.lang.annotation.*;

/**
 * 完整title 为
 * 1  prefix
 * 2  value（除exclude）
 * 3  suffix
 * 拼接结果
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface AutoMapping {

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
	 * 是否独占绑定结果
	 *
	 * @return 默认共享
	 */
	BindType bindType() default BindType.Nonexclusive;


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
	String value() default "";

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
	 * Y 两层(最多两层)
	 * N 一层
	 *
	 * @return
	 */
	Complex complex() default Complex.N;

	/**
	 * 读写属性配置
	 *
	 * @return
	 */
	RWType rw() default RWType.READ_WRITE;
}
