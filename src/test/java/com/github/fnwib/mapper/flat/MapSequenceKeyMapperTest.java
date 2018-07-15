package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.databing.title.Sequence;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class MapSequenceKeyMapperTest {

	@Test
	public void getValue() {
	}

	@Test
	public void getContents() {
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType javaType = typeFactory.constructMapType(Map.class, Sequence.class, String.class);
		BindColumn column1 = new BindColumn(1, "test 1", "1");
		BindColumn column2 = new BindColumn(2, "test 2", "2");
		BindColumn column3 = new BindColumn(3, "test 3", "3");
		List<BindColumn> columns = Lists.newArrayList(column1, column2, column3);
		AbstractContainerMapper mapper = new MapSequenceKeyMapper("1", javaType, columns, Collections.emptyList());
		Map<Sequence, String> value = Maps.newHashMap();
		value.put(new Sequence(1), "va");
		value.put(new Sequence(2), null);
		value.put(new Sequence(3), "va2");
		List<ExcelContent> contents = mapper.getContents(value);
		Assert.assertEquals("map<Sequence,String> to  contents", 3, contents.size());
		contents.sort(Comparator.comparing(ExcelContent::getColumnIndex));
		List<ExcelContent> expected = Lists.newArrayList();
		expected.add(new ExcelContent(1, "va"));
		expected.add(new ExcelContent(2, null));
		expected.add(new ExcelContent(3, "va2"));
		Assert.assertEquals("map<Sequence,String> to  contents", expected, contents);
	}

}