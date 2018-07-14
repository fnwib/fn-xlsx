package com.github.fnwib.mapping.impl;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static org.junit.Assert.*;

public class CollectionMappingTest {

	List<BindColumn> columns;
	JavaType contentType;

	@Before
	public void initDate() {
		BindColumn column = new BindColumn(1, "test-1", "1");
		BindColumn column2 = new BindColumn(2, "test-2", "2");
		BindColumn column3 = new BindColumn(3, "test-3", "3");
		columns = Lists.newArrayList(column, column2, column3);
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		contentType = typeFactory.constructType(String.class);
	}

	@Test
	public void getColumns() {
		CollectionMapping mapping = new CollectionMapping(contentType, columns, Collections.emptyList());
		List<BindColumn> columns = mapping.getColumns();
		Assert.assertEquals("columns", this.columns, columns);
	}

	@Test
	public void getValue() {
	}

	@Test
	public void getContents() {
		CollectionMapping mapping = new CollectionMapping(contentType, columns, Collections.emptyList());
		List<Integer> value = Lists.newArrayList(null, 1, 2);
		List<ExcelContent> contents = mapping.getContents(value);
		Assert.assertEquals("string value to  contents", 3, contents.size());
		contents.sort(Comparator.comparing(ExcelContent::getColumnIndex));
		List<ExcelContent> expected = Lists.newArrayList();
		expected.add(new ExcelContent(1, null));
		expected.add(new ExcelContent(2, "1"));
		expected.add(new ExcelContent(3, "2"));
		Assert.assertEquals("map<Integer,String> to  contents", expected, contents);
	}
}