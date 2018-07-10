package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.mapping.impl.BindMapping;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public class BindProperty {
	/**
	 * 改配置属于哪个类
	 */
	private Class<?> region;

	/**
	 * 字段带泛型类型
	 */
	private JavaType type;
	/**
	 * 字段值name
	 */
	private String name;
	/**
	 * 获取行号或者cell 取值
	 */
	private Operation operation;
	/**
	 * String 的值处理器列表
	 */
	private Collection<ValueHandler> valueHandlers;

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
	/**
	 *
	 */
	private BindMapping bindMapping;

	private Method readMethod;

	private Method writeMethod;

	/**
	 * 判断该属性是否属于
	 *
	 * @param bindClass
	 * @return
	 */
	public boolean isRegion(Class<?> bindClass) {
		return region == bindClass;
	}

	public JavaType getType() {
		return type;
	}

	public String getName() {
		return name;
	}

	public Operation getOperation() {
		return operation;
	}

	public Collection<ValueHandler> getValueHandlers() {
		return valueHandlers;
	}

	public String getPrefix() {
		return prefix;
	}

	public String getTitle() {
		return title;
	}

	public String getSuffix() {
		return suffix;
	}

	public String getExclude() {
		return exclude;
	}

	public BindMapping getBindMapping() {
		return bindMapping;
	}

	public void setBindMapping(BindMapping bindMapping) {
		this.bindMapping = bindMapping;
	}

	public Method getReadMethod() {
		return readMethod;
	}

	public void setReadMethod(Method readMethod) {
		this.readMethod = readMethod;
	}

	public Method getWriteMethod() {
		return writeMethod;
	}

	public void setWriteMethod(Method writeMethod) {
		this.writeMethod = writeMethod;
	}

	public static Builder builder() {
		return new Builder();
	}

	private BindProperty(Builder builder) {
		region = builder.region;
		type = builder.type;
		name = builder.name;
		operation = builder.operation;
		valueHandlers = Objects.isNull(builder.valueHandlers) ? Collections.emptyList() : builder.valueHandlers;
		prefix = Objects.isNull(builder.prefix) ? StringUtils.EMPTY : builder.prefix;
		title = Objects.isNull(builder.title) ? StringUtils.EMPTY : builder.title;
		suffix = Objects.isNull(builder.suffix) ? StringUtils.EMPTY : builder.suffix;
		exclude = Objects.isNull(builder.exclude) ? StringUtils.EMPTY : builder.exclude;
	}


	public static final class Builder {
		private Class<?> region;
		private JavaType type;
		private String name;
		private Operation operation;
		private Collection<ValueHandler> valueHandlers;
		private String prefix;
		private String title;
		private String suffix;
		private String exclude;

		public Builder() {
		}

		public Builder region(Class<?> val) {
			region = val;
			return this;
		}

		public Builder type(JavaType val) {
			type = val;
			return this;
		}

		public Builder name(String val) {
			name = val;
			return this;
		}

		public Builder operation(Operation val) {
			operation = val;
			return this;
		}

		public Builder valueHandlers(Collection<ValueHandler> val) {
			valueHandlers = val;
			return this;
		}

		public Builder prefix(String val) {
			prefix = val;
			return this;
		}

		public Builder title(String val) {
			title = val;
			return this;
		}

		public Builder suffix(String val) {
			suffix = val;
			return this;
		}

		public Builder exclude(String val) {
			exclude = val;
			return this;
		}

		public BindProperty build() {
			return new BindProperty(this);
		}
	}
}
