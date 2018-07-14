package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapIndexKeyMappingTest {
	@Test
	public void getValue() {
	}

	@Test
	public void getContents() {
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType javaType = typeFactory.constructMapType(Map.class, Integer.class, String.class);
		BindColumn column1 = new BindColumn(1, "test 1", "1");
		BindColumn column2 = new BindColumn(2, "test 2", "2");
		BindColumn column3 = new BindColumn(3, "test 3", "3");
		List<BindColumn> columns = Lists.newArrayList(column1, column2, column3);
		AbstractMapMapping mapping = new MapIndexKeyMapping(javaType, columns, Collections.emptyList());
		Map<Integer, String> value = Maps.newHashMap();
		value.put(1, "va");
		value.put(2, "va1");
		value.put(3, "va2");
		List<ExcelContent> contents = mapping.getContents(value);
		Assert.assertEquals("map<Integer,String> to  contents", 3, contents.size());
		contents.sort(Comparator.comparing(ExcelContent::getColumnIndex));
		List<ExcelContent> expected = Lists.newArrayList();
		expected.add(new ExcelContent(1, "va"));
		expected.add(new ExcelContent(2, "va1"));
		expected.add(new ExcelContent(3, "va2"));
		Assert.assertEquals("map<Integer,String> to  contents", expected, contents);
	}
}