package com.github.fnwib.mapping.model;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

@ToString
@Builder
@Getter
public class Rule {

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

	public Rule(String prefix, String title, String suffix, String exclude) {
		this.prefix = Objects.isNull(prefix) ? StringUtils.EMPTY : prefix;
		this.title = Objects.isNull(title) ? StringUtils.EMPTY : title;
		this.suffix = Objects.isNull(suffix) ? StringUtils.EMPTY : suffix;
		this.exclude = Objects.isNull(exclude) ? StringUtils.EMPTY : exclude;
	}
}
