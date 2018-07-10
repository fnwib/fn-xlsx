package com.github.fnwib.mapping;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.Operation;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;
import java.util.Objects;

public class BindParam {

	private JavaType type;

	private String name;

	private Operation operation;

	private Collection<ValueHandler> valueHandlers;

	private String prefix;
	/**
	 * title 支持正则
	 *
	 * @return
	 */
	private String title;

	private String suffix;

	private String exclude;

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

	public static Builder builder() {
		return new Builder();
	}

	private BindParam(Builder builder) {
		type = builder.type;
		name = builder.name;
		operation =builder.operation;
		valueHandlers = builder.valueHandlers;
		prefix = Objects.isNull(builder.prefix) ? StringUtils.EMPTY : builder.prefix;
		title = Objects.isNull(builder.title) ? StringUtils.EMPTY : builder.title;
		suffix = Objects.isNull(builder.suffix) ? StringUtils.EMPTY : builder.suffix;
		exclude = Objects.isNull(builder.exclude) ? StringUtils.EMPTY : builder.exclude;
	}


	public static final class Builder {
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

		public BindParam build() {
			return new BindParam(this);
		}
	}
}
