package com.github.fnwib.mapper.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * AutoMapping注解的绑定配置参数
 */
@ToString
@Builder
@Getter
public class MatchConfig {

	/**
	 * 匹配参数的前缀
	 */
	private String prefix;
	/**
	 * 匹配参数的值 (正则匹配)
	 */
	private String title;
	/**
	 * 匹配参数的后缀
	 */
	private String suffix;
	/**
	 * 匹配参数的忽略的值 (正则匹配)
	 */
	private String exclude;

	public MatchConfig(String prefix, String title, String suffix, String exclude) {
		this.prefix = Objects.isNull(prefix) ? StringUtils.EMPTY : prefix;
		this.title = Objects.isNull(title) ? StringUtils.EMPTY : title;
		this.suffix = Objects.isNull(suffix) ? StringUtils.EMPTY : suffix;
		this.exclude = Objects.isNull(exclude) ? StringUtils.EMPTY : exclude;
	}
}
