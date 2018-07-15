package com.github.fnwib.mapper.nested;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.mapper.Mappings;
import com.github.fnwib.write.model.ExcelContent;
import com.github.fnwib.write.model.ExcelHeader;
import com.github.fnwib.write.model.ExcelHeaderCreater;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class NestedMapperTest {

	@Test
	public void getColumns() {
	}

	@Test
	public void getValue() {
	}

	@Test
	public void getContents() {
		List<ExcelHeader> headers = ExcelHeaderCreater.create(new AtomicInteger(), "list 1", "list 2", "list 3", "map 1", "map 2", "map 3");
		NestedMapper<TestNestedModel> nestedMapper = Mappings.createNestedMapper(TestNestedModel.class, new LocalConfig(), headers);

		List<ExcelContent> contents = nestedMapper.getContents(get());
		Assert.assertEquals("TestNestedModel to  contents", 6, contents.size());
		contents.sort(Comparator.comparing(ExcelContent::getColumnIndex));
		List<ExcelContent> expected = Lists.newArrayList();
		expected.add(new ExcelContent(0, null));
		expected.add(new ExcelContent(1, "val2"));
		expected.add(new ExcelContent(2, "val3"));
		expected.add(new ExcelContent(3, "m1"));
		expected.add(new ExcelContent(4, null));
		expected.add(new ExcelContent(5, "m3"));
		Assert.assertEquals("TestNestedModel to  contents", expected, contents);
	}

	@Getter
	@Setter
	private static class TestNestedModel {
		@AutoMapping(prefix = "list", value = "\\d+")
		private List<String> list;
		@AutoMapping(prefix = "map", value = "\\d+")
		private Map<Integer, String> map;
	}

	private TestNestedModel get() {
		TestNestedModel m = new TestNestedModel();
		m.setList(Lists.newArrayList(null, "val2", "val3"));
		Map<Integer, String> map = Maps.newHashMap();
		map.put(3, "m1");
		map.put(4, null);
		map.put(5, "m3");
		m.setMap(map);
		return m;
	}
}