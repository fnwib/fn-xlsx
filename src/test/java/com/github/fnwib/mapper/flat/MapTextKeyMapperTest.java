package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.util.UUIDUtils;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapTextKeyMapperTest {

	@Test
	public void getValue() {
	}

	@Test
	public void getContents() {
		String key1 = UUIDUtils.getHalfId();
		String key2 = UUIDUtils.getHalfId();
		String key3 = UUIDUtils.getHalfId();
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType javaType = typeFactory.constructMapType(Map.class, String.class, String.class);
		BindColumn column1 = new BindColumn(1, key1, "1");
		BindColumn column2 = new BindColumn(2, key2, "2");
		BindColumn column3 = new BindColumn(3, key3, "3");
		List<BindColumn> columns = Lists.newArrayList(column1, column2, column3);
		AbstractContainerMapper mapping = new MapTextKeyMapper("1",javaType, columns, Collections.emptyList());
		Map<String, String> value = Maps.newHashMap();
		value.put(key1, "va");
		value.put(key2, "va1");
		value.put(key3, "va2");
		List<ExcelContent> contents = mapping.getContents(value);
		Assert.assertEquals("map<String,String> to  contents", 3, contents.size());
		contents.sort(Comparator.comparing(ExcelContent::getColumnIndex));
		List<ExcelContent> expected = Lists.newArrayList();
		expected.add(new ExcelContent(1, "va"));
		expected.add(new ExcelContent(2, "va1"));
		expected.add(new ExcelContent(3, "va2"));
		Assert.assertEquals("map<String,String> to  contents", expected, contents);
	}
}