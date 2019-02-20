package com.github.fnwib.util;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ComplexEnum;
import com.github.fnwib.model.Header;
import com.github.fnwib.write.CommonPath;
import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class FnUtilsTest extends CommonPath {

	@Test
	public void getHeaders() {
		List<Header> headers = FnUtils.getHeaders(new AtomicInteger(1), A.class);
		for (Header header : headers) {
			System.out.println(header);
		}
		List<Integer> collect = headers.stream().map(Header::getColumnIndex).collect(Collectors.toList());
		Assert.assertEquals(Lists.newArrayList(1, 2, 3, 4), collect);

		List<String> collect2 = headers.stream().map(Header::getValue).collect(Collectors.toList());
		Assert.assertEquals(Lists.newArrayList("a", "e", "f", "c"), collect2);

	}

	@Getter
	@Setter
	static class A {
		@AutoMapping(value = "a", order = 0)
		private String a;
		@AutoMapping(value = "b", order = 1, complex = ComplexEnum.NESTED)
		private B b;
		@AutoMapping(value = "c", order = 2)
		private String c;
	}

	@Getter
	@Setter
	static class B {
		@AutoMapping(value = "e", order = 0)
		private String c;
		@AutoMapping(value = "f", order = 1)
		private String d;
	}

}