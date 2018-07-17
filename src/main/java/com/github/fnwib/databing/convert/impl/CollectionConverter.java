package com.github.fnwib.databing.convert.impl;

import com.github.fnwib.databing.convert.PropertyConverter;
import com.github.fnwib.databing.title.CellTitle;
import com.github.fnwib.databing.valuehandler.ValueHandler;
import com.github.fnwib.exception.SettingException;
import com.github.fnwib.reflect.Property;
import com.github.fnwib.write.CellText;
import com.google.common.collect.Lists;
import org.apache.poi.ss.usermodel.Row;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CollectionConverter implements PropertyConverter {

	private static final Logger log = LoggerFactory.getLogger(CollectionConverter.class);

	private final Property property;
	private final int titlesSize;
	private final List<BeanConverter> converters;
	private final List<CellText> emptyCellTexts;


	public CollectionConverter(Property property,
							   List<Integer> bindColumns,
							   Collection<ValueHandler> valueHandlers) {
		this.property = property;
		this.titlesSize = bindColumns.size();
		this.converters = Lists.newArrayListWithCapacity(bindColumns.size());
		this.emptyCellTexts = Lists.newArrayListWithCapacity(bindColumns.size());
		for (Integer column : bindColumns) {
			BeanConverter converter = new BeanConverter(property, property.getContentType(), column, valueHandlers);
			converters.add(converter);
			emptyCellTexts.add(new CellText(column, ""));
		}
	}

	@Override
	public boolean isMatched() {
		return titlesSize > 0;
	}

	@Override
	public int num() {
		return titlesSize;
	}

	@Override
	public String getKey() {
		return property.getName();
	}

	@Override
	public Optional<Collection<String>> getValue(Row row) {
		if (!isMatched()) {
			return Optional.of(Collections.emptyList());
		}
		Collection<String> list = Lists.newArrayListWithCapacity(converters.size());
		for (BeanConverter converter : converters) {
			if (converter.isMatched()) {
				Optional<String> value = converter.getValue(row);
				list.add(value.orElse(""));
			}
		}
		return Optional.of(list);
	}


	@Override
	@SuppressWarnings("unchecked")
	public <T> List<CellText> getCellText(T element) {
		if (!isMatched()) {
			return emptyCellTexts;
		}
		try {
			Object value = property.getReadMethod().invoke(element);
			if (value == null) {
				return emptyCellTexts;
			}
			List<Object> objects = (List<Object>) value;
			if (titlesSize < objects.size()) {
				log.error("-->property name is {} , matched tile size is [{}] , value is [{}]",
						property.getName(), titlesSize, element);
				throw new SettingException("参数长度大于可写入数据长度");
			}
			List<CellText> cellTexts = Lists.newArrayListWithCapacity(objects.size());
			int i = 0;
			for (Object object : objects) {
				BeanConverter converter = converters.get(i);
				Optional<CellText> optional = converter.getSingleCellText(object);
				optional.ifPresent(c -> cellTexts.add(c));
				i++;
			}
			return cellTexts;
		} catch (IllegalAccessException | InvocationTargetException e) {
			log.error("invoke error ", e);
			return emptyCellTexts;
		}
	}

}
