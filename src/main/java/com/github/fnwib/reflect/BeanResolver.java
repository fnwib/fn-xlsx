package com.github.fnwib.reflect;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.exception.PropertyException;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.plugin.ValueHandler;
import com.github.fnwib.util.ValueUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public enum BeanResolver {

	INSTANCE;
	private static final Logger log = LoggerFactory.getLogger(BeanResolver.class);

	private final Map<Class<?>, List<Property>> types = new ConcurrentHashMap<>();

	public static JavaType getInterfaceGenericType(Class<?> clazz) {
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		Type type = clazz.getGenericInterfaces()[0];
		TypeBindings bindings = typeFactory.constructType(type).getBindings();
		JavaType boundType = bindings.getBoundType(0);
		if (boundType != null) {
			return typeFactory.constructType(boundType);
		} else {
			throw new SettingException();
		}
	}

	public <T> T format(T param) {
		try {
			Class<?> paramClass = param.getClass();
			Constructor<?> constructor = paramClass.getConstructor();
			T newInstance = (T) constructor.newInstance();
			List<Property> properties = getProperties(paramClass);
			for (Property property : properties) {
				PropertyDescriptor propertyDescriptor = property.getPropertyDescriptor();
				Method readMethod = propertyDescriptor.getReadMethod();
				Collection<ValueHandler> valueHandlers = property.getValueHandlers();
				Method writeMethod = propertyDescriptor.getWriteMethod();
				final Object value = readMethod.invoke(param);
				if (property.getFieldType().getRawClass() != String.class) {
					writeMethod.invoke(newInstance, value);
				} else {
					Optional<String> newValue = ValueUtil.getStringValue(((String) value), valueHandlers);
					if (newValue.isPresent()) {
						writeMethod.invoke(newInstance, newValue.get());
					}
				}
			}
			return newInstance;
		} catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
			log.error("copy value error", e);
			throw new PropertyException(e);
		}
	}


	private synchronized List<Property> resolve(final Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("参数不能为null");
		}
		List<Property> properties = Lists.newArrayList();
		Set<Class<?>> ca = Sets.newHashSet();
		setSuperClassWithoutObject(clazz, ca);
		for (Class<?> aClass : ca) {
			resolve(clazz, aClass, properties);
		}
		return properties;
	}

	private void setSuperClassWithoutObject(final Class<?> clazz, Set<Class<?>> ca) {
		if (!ca.contains(clazz)) {
			ca.add(clazz);
		}
		Class<?> superclass = clazz.getSuperclass();
		if (superclass != null && superclass != Object.class) {
			setSuperClassWithoutObject(superclass, ca);
		}
	}

	private void resolve(final Class<?> region, final Class<?> clazz, List<Property> properties) {
		try {
			TypeFactory typeFactory = TypeFactory.defaultInstance();
			Field[] fields = clazz.getDeclaredFields();
			PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(clazz)
					.getPropertyDescriptors();
			for (PropertyDescriptor descriptor : propertyDescriptors) {
				for (Field field : fields) {
					if (field.getName().equalsIgnoreCase(descriptor.getName())) {
						Type genericType = field.getGenericType();
						JavaType javaType = typeFactory.constructType(genericType);
						Property property = new Property(region, field, javaType, descriptor);
						properties.add(property);
					}
				}
			}
		} catch (IntrospectionException e) {
			throw new PropertyException(e);
		}
	}

	public synchronized List<Property> getProperties(final Class<?> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("参数不能为null");
		}
		if (types.containsKey(clazz)) {
			return types.get(clazz);
		} else {
			List<Property> resolve = resolve(clazz);
			types.put(clazz, resolve);
			return resolve;
		}
	}

	public List<Property> getPropertiesWithAnnotation(final Class<?> clazz, final Class<? extends Annotation> annotationType) {
		if (clazz == null || annotationType == null) {
			throw new IllegalArgumentException("参数不能为null");
		}
		List<Property> properties = getProperties(clazz);
		return properties.stream()
				.filter(property -> property.getField().getAnnotation(annotationType) != null).collect(Collectors.toList());

	}


}
