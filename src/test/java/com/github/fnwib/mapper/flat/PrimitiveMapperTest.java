package com.github.fnwib.mapper.flat;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import org.junit.Assert;
import org.junit.Test;

import java.util.Collections;
import java.util.List;

public class PrimitiveMapperTest {

	@Test
	public void getValue() {
	}

	@Test
	public void getContents() {
		TypeFactory typeFactory = TypeFactory.defaultInstance();
		JavaType javaType = typeFactory.constructType(String.class);
		BindColumn column = new BindColumn(1, "test -", "1");
		PrimitiveMapper mapping = new PrimitiveMapper(javaType, column, Collections.emptyList());
		List<ExcelContent> contents = mapping.getContents("va");
		Assert.assertEquals("string value to  contents", 1, contents.size());
		Assert.assertEquals("string value to  contents", new ExcelContent(1, "va"), contents.get(0));
	}
}