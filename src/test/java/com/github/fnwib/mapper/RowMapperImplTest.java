package com.github.fnwib.mapper;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.context.LocalConfig;
import com.github.fnwib.model.Header;
import com.github.fnwib.model.HeaderCreator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class RowMapperImplTest {


	@Test
	public void match() {
		RowMapperImpl<RowMapperBo> mapper = new RowMapperImpl<>(RowMapperBo.class, new LocalConfig(), 1,10);
		List<Header> headers = HeaderCreator.create(new AtomicInteger(), "value", "value1", "value");
		boolean match = mapper.match(headers);
		Assert.assertTrue("", match);
	}

	static class RowMapperBo {
		@AutoMapping("value")
		private String value;

		public String getValue() {
			return value;
		}

		public void setValue(String value) {
			this.value = value;
		}
	}

}