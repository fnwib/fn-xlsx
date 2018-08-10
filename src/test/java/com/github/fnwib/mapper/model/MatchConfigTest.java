package com.github.fnwib.mapper.model;

import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class MatchConfigTest {

	@Test
	public void match() {
		MatchConfig matchConfig = MatchConfig.builder().prefix("aa(").middle("a").build();
		Map<Integer, String> row = Maps.newHashMap();
		row.put(1, "aaa");
		row.put(2, "aa(a");
		List<BindColumn> rows = matchConfig.match(row);
		Assert.assertEquals("", 1, rows.size());
		Assert.assertEquals("", 2, rows.get(0).getIndex());
	}

	@Test
	public void match1() {
		MatchConfig matchConfig = MatchConfig.builder().prefix("aa").middle("a").build();
		Map<Integer, String> row = Maps.newHashMap();
		row.put(1, "aaa");
		row.put(2, "aa(a");
		List<BindColumn> rows = matchConfig.match(row);
		Assert.assertEquals("", 1, rows.size());
		Assert.assertEquals("", 1, rows.get(0).getIndex());
	}

	@Test
	public void match2() {
		MatchConfig matchConfig = MatchConfig.builder().prefix("aa ").middle("a").build();
		Map<Integer, String> row = Maps.newHashMap();
		row.put(1, "aaa");
		row.put(2, "aa(a");
		List<BindColumn> rows = matchConfig.match(row);
		Assert.assertEquals("", 0, rows.size());
	}
}