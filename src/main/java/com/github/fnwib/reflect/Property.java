package com.github.fnwib.reflect;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.CellType;
import com.github.fnwib.annotation.Complex;
import com.github.fnwib.annotation.ReadValueHandler;
import com.github.fnwib.databing.title.match.TitleMatcher;
import com.github.fnwib.databing.title.match.TitleMatcherImpl;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapping.model.BindProperty;
import com.github.fnwib.mapping.model.Rule;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class Property {
	private final Class<?> region;
	@Getter
	private final Field field;
	private final JavaType fieldType;
	private final PropertyDescriptor propertyDescriptor;

	public Property(Class<?> region, Field field, JavaType fieldType, PropertyDescriptor propertyDescriptor) {
		this.region = Objects.requireNonNull(region);
		this.field = Objects.requireNonNull(field);
		this.fieldType = Objects.requireNonNull(fieldType);
		this.propertyDescriptor = Objects.requireNonNull(propertyDescriptor);
	}

	public Optional<TitleMatcher> getTitleMatcher() {
		CellType cellType = field.getAnnotation(CellType.class);
		AutoMapping mapping = field.getAnnotation(AutoMapping.class);
		if (mapping != null) {
			return Optional.of(new TitleMatcherImpl(mapping));
		} else {
			if (cellType != null) {
				return Optional.of(new TitleMatcherImpl(cellType));
			}
		}
		return Optional.empty();
	}

	public Optional<BindProperty> toBindParam() {
		AutoMapping mapping = field.getAnnotation(AutoMapping.class);
		if (mapping == null) {
			return Optional.empty();
		}
		Rule rule = Rule.builder().title(mapping.value())
				.suffix(mapping.suffix())
				.prefix(mapping.prefix())
				.exclude(mapping.exclude())
				.build();
		BindProperty param = BindProperty.builder()
				.region(region)
				.propertyName(getName())
				.type(getFieldType())
				.valueHandlers(getValueHandlers())
				.operation(mapping.operation())
				.order(mapping.order())
				.complex(mapping.complex())
				.rule(rule)
				.build();
		return Optional.of(param);

	}

	public Collection<ValueHandler> getValueHandlers() {
		ReadValueHandler handler = field.getAnnotation(ReadValueHandler.class);
		if (handler == null) {
			return Collections.emptyList();
		}
		Collection<ValueHandler> valueHandlers = Lists.newArrayListWithCapacity(handler.value().length);
		for (Class<? extends ValueHandler> h : handler.value()) {
			Constructor<?>[] constructors = h.getConstructors();
			if (constructors.length == 1) {
				Constructor<?> constructor = constructors[0];
				try {
					ValueHandler valueHandler = (ValueHandler) constructor.newInstance();
					valueHandlers.add(valueHandler);
				} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
					throw new SettingException(h.getName() + " no found non args constructor");
				}
			} else {
				throw new SettingException(h.getName() + " not support multi args constructor");
			}
		}
		return valueHandlers;
	}

	/**
	 * please use getFieldType()
	 *
	 * @return
	 */
	@Deprecated
	public JavaType getJavaType() {
		return fieldType;
	}

	public JavaType getFieldType() {
		return fieldType;
	}

	public PropertyDescriptor getPropertyDescriptor() {
		return propertyDescriptor;
	}

	public JavaType getContentType() {
		return fieldType.getContentType();
	}

	public String getName() {
		return field.getName();
	}

	public <T extends Annotation> T getAnnotation(final Class<T> annotationCls) {
		return field.getAnnotation(annotationCls);
	}

	public Method getReadMethod() {
		return propertyDescriptor.getReadMethod();
	}


}
