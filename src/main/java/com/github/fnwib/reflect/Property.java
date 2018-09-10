package com.github.fnwib.reflect;

import com.fasterxml.jackson.databind.JavaType;
import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ReadValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.mapper.model.BindProperty;
import com.github.fnwib.mapper.model.FeatureConfig;
import com.github.fnwib.mapper.model.MatchConfig;
import com.github.fnwib.plugin.ValueHandler;
import com.google.common.collect.Lists;
import lombok.Getter;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;

public class Property {
	@Getter
	private final Class<?> region;
	@Getter
	private final Field field;
	private final JavaType fieldType;
	private final PropertyDescriptor propertyDescriptor;
	private Collection<ValueHandler> valueHandlers;

	public Property(Class<?> region, Field field, JavaType fieldType, PropertyDescriptor propertyDescriptor) {
		this.region = Objects.requireNonNull(region);
		this.field = Objects.requireNonNull(field);
		this.fieldType = Objects.requireNonNull(fieldType);
		this.propertyDescriptor = Objects.requireNonNull(propertyDescriptor);
	}

	public Optional<BindProperty> toBindParam() {
		AutoMapping mapping = field.getAnnotation(AutoMapping.class);
		if (mapping != null) {
			return toBindParam(mapping);
		}
		return Optional.empty();
	}

	private Optional<BindProperty> toBindParam(AutoMapping mapping) {
		MatchConfig matchConfig = MatchConfig.builder().middle(mapping.value())
				.suffix(mapping.suffix())
				.prefix(mapping.prefix())
				.exclude(mapping.exclude())
				.build();
		FeatureConfig featureConfig = FeatureConfig.builder()
				.order(mapping.order())
				.complex(mapping.complex())
				.shared(mapping.shared())
				.readonly(mapping.readonly())
				.build();
		BindProperty param = BindProperty.builder()
				.property(this)
				.operation(mapping.operation())
				.valueHandlers(getValueHandlers())
				.matchConfig(matchConfig)
				.featureConfig(featureConfig)
				.build();
		return Optional.of(param);

	}

	public Collection<ValueHandler> getValueHandlers() {
		ReadValueHandler handler = field.getAnnotation(ReadValueHandler.class);
		if (handler == null) {
			return Collections.emptyList();
		}
		if (valueHandlers == null) {
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
			this.valueHandlers = valueHandlers;
		}
		return Lists.newArrayList(valueHandlers);
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

	public Method getWriteMethod() {
		return propertyDescriptor.getWriteMethod();
	}


}
