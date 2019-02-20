package com.github.fnwib.write;

import com.github.fnwib.annotation.AutoMapping;
import com.github.fnwib.annotation.ComplexEnum;
import com.github.fnwib.model.Content;
import com.google.common.collect.Lists;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class ExcelTypeWriterTest extends CommonPath {

	@Test
	public void toContents() {
		ExcelTypeWriter writer = ExcelTypeWriter.create(null, basePath, A.class, B.class);
		A a = new A("a1", new B("a2", "a3"), "a4");
		B b = new B("a5", "a6");
		List<Content> contents = writer.toContents(Lists.newArrayList(a,b));
		int i = 0;
		for (Content content : contents) {
			System.out.println(content);
			Assert.assertEquals(i, content.getColumnIndex());
			i++;
			Assert.assertEquals("a" + i, content.getValue());

		}
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class A {
		@AutoMapping(value = "a", order = 0)
		private String a;
		@AutoMapping(value = "b", order = 1, complex = ComplexEnum.NESTED)
		private B b;
		@AutoMapping(value = "c", order = 2)
		private String c;
	}

	@Getter
	@Setter
	@AllArgsConstructor
	@NoArgsConstructor
	public static class B {
		@AutoMapping(value = "e", order = 0)
		private String c;
		@AutoMapping(value = "f", order = 1)
		private String d;
	}
}