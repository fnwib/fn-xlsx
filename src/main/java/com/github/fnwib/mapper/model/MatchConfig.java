package com.github.fnwib.mapper.model;

import com.github.fnwib.util.ValueUtil;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * AutoMapping注解的绑定配置参数
 */
@ToString
public class MatchConfig {

	/**
	 * 匹配参数的前缀
	 */
	private String prefix;
	/**
	 * 匹配参数的值 (正则匹配)
	 */
	private String middle;
	private Pattern middlePattern;
	/**
	 * 匹配参数的后缀
	 */
	private String suffix;
	/**
	 * 匹配参数的忽略的值 (正则匹配)
	 */
	private String exclude;
	private Pattern excludePattern;

	public MatchConfig(String prefix, String middle, String suffix, String exclude) {
		this.prefix = Objects.isNull(prefix) ? StringUtils.EMPTY : prefix;
		this.middle = Objects.isNull(middle) ? StringUtils.EMPTY : middle;
		this.suffix = Objects.isNull(suffix) ? StringUtils.EMPTY : suffix;
		this.exclude = Objects.isNull(exclude) ? StringUtils.EMPTY : exclude;
		this.middlePattern = Pattern.compile(this.middle, Pattern.CASE_INSENSITIVE);
		this.excludePattern = Pattern.compile(this.exclude, Pattern.CASE_INSENSITIVE);
	}

	public static MatchConfig.Builder builder() {
		return new MatchConfig.Builder();
	}

	public MatchConfig(Builder builder) {
		this(builder.prefix, builder.middle, builder.suffix, builder.exclude);
	}

	public List<BindColumn> match(Map<Integer, String> row) {
		if (StringUtils.isBlank(prefix) && StringUtils.isBlank(middle) && StringUtils.isBlank(suffix)) {
			return Collections.emptyList();
		}
		List<BindColumn> bindColumns = new ArrayList<>();
		row.forEach((columnIndex, value) -> {
			Optional<String> root = ValueUtil.substringBetweenIgnoreCase(value, prefix, suffix);
			if (!root.isPresent()) {
				return;
			}
			String mid = StringUtils.trimToEmpty(root.get());
			Matcher titleMatcher = middlePattern.matcher(mid);
			if (titleMatcher.matches()) {
				if (StringUtils.isNotBlank(exclude) && excludePattern.matches(exclude, root.get().trim())) {
					return;
				}
				bindColumns.add(new BindColumn(columnIndex, value, mid));
			}
		});
		return bindColumns;
	}

	public static final class Builder {
		/**
		 * 匹配参数的前缀
		 */
		private String prefix;
		/**
		 * 匹配参数的值 (正则匹配)
		 */
		private String middle;
		/**
		 * 匹配参数的后缀
		 */
		private String suffix;
		/**
		 * 匹配参数的忽略的值 (正则匹配)
		 */
		private String exclude;

		public MatchConfig.Builder prefix(String val) {
			prefix = val;
			return this;
		}

		public MatchConfig.Builder middle(String val) {
			middle = val;
			return this;
		}

		public MatchConfig.Builder suffix(String val) {
			suffix = val;
			return this;
		}

		public MatchConfig.Builder exclude(String val) {
			exclude = val;
			return this;
		}

		public MatchConfig build() {
			return new MatchConfig(this);
		}
	}
}
