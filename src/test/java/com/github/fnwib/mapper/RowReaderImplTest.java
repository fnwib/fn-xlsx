package com.github.fnwib.mapper;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.model.Header;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class RowReaderImplTest {

	@Test
	public void match() {

		RowReader reader = new RowReaderImpl<>(Model.class);
		List<Header> header = Lists.newArrayList();
		Header name = Header.builder().columnIndex(0).value("name").build();
		header.add(name);
		boolean match = reader.match(header);
		Assert.assertTrue(match);
	}

	@Getter
	@Setter
	static class Model {
		@AutoMapping(value = "name")
		private String name;
	}
}