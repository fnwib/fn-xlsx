package com.github.fnwib.mapping.flat;

import com.github.fnwib.mapping.model.BindColumn;
import com.github.fnwib.write.model.ExcelContent;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

public class LineNumMappingTest {
	List<BindColumn> columns;

	@Before
	public void initDate() {
		BindColumn column = new BindColumn(1, "test-1", "1");
		columns = Lists.newArrayList(column);
	}

	@Test
	public void getColumns() {
		LineNumMapping mapping = new LineNumMapping(columns);
		List<BindColumn> columns = mapping.getColumns();
		Assert.assertEquals("columns", this.columns, columns);
	}

	@Test
	public void getValue() {
	}

	@Test
	public void getContents() {
		LineNumMapping mapping = new LineNumMapping(columns);
		List<ExcelContent> contents = mapping.getContents(1);
		Assert.assertEquals("string value to  contents", 1, contents.size());
		Assert.assertEquals("string value to  contents", new ExcelContent(1, "1"), contents.get(0));
	}
}