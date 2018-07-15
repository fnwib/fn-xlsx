package com.github.fnwib.mapper;

import com.github.fnwib.databing.Context;
import com.github.fnwib.databing.LocalConfig;
import com.github.fnwib.mapper.model.BindColumn;
import com.github.fnwib.mapper.model.MatchConfig;
import com.google.common.collect.Maps;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class FnMatcherTest {

	@Test
	public void match() {
		LocalConfig localConfig = Context.INSTANCE.getContextConfig();
		MatchConfig param = MatchConfig.builder().prefix("aa(").title("a").build();
		FnMatcher fnMatcher = new FnMatcher(param, localConfig);
		Map<Integer, String> row = Maps.newHashMap();
		row.put(1, "aaa");
		row.put(2, "aa(a");
		List<BindColumn> match = fnMatcher.match(row);
		Assert.assertEquals("", 1, match.size());
		Assert.assertEquals("", 2, match.get(0).getIndex());
	}

	@Test
	public void match1() {
		LocalConfig localConfig = Context.INSTANCE.getContextConfig();
		MatchConfig param = MatchConfig.builder().prefix("aa").title("a").build();
		FnMatcher fnMatcher = new FnMatcher(param, localConfig);
		Map<Integer, String> row = Maps.newHashMap();
		row.put(1, "aaa");
		row.put(2, "aa(a");
		List<BindColumn> match = fnMatcher.match(row);
		Assert.assertEquals("", 1, match.size());
		Assert.assertEquals("", 1, match.get(0).getIndex());
	}

	@Test
	public void match2() {
		LocalConfig localConfig = Context.INSTANCE.getContextConfig();
		MatchConfig param = MatchConfig.builder().prefix("aa ").title("a").build();
		FnMatcher fnMatcher = new FnMatcher(param, localConfig);
		Map<Integer, String> row = Maps.newHashMap();
		row.put(1, "aaa");
		row.put(2, "aa(a");
		List<BindColumn> match = fnMatcher.match(row);
		Assert.assertEquals("", 0, match.size());
	}
}